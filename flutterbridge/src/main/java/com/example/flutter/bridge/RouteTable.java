package com.example.flutter.bridge;

import android.app.Activity;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Registers and resolves the in-app URL scheme before any screen is opened. */
public final class RouteTable {
    @FunctionalInterface
    public interface NativeDestination {
        void open(Activity activity, Request route);
    }

    public static final class UnknownRoute extends IllegalArgumentException {
        UnknownRoute(String url) {
            super("No page is registered for " + url);
        }
    }

    public static final class Request {
        public final String url;
        public final Map<String, String> path;
        public final Map<String, String> query;

        Request(String url, Map<String, String> path, Map<String, String> query) {
            this.url = url;
            this.path = Collections.unmodifiableMap(path);
            this.query = Collections.unmodifiableMap(query);
        }

        public int positiveInt(String key) {
            String value = path.get(key);
            if (value != null && value.matches("[1-9][0-9]*")) {
                try {
                    return Integer.parseInt(value);
                } catch (NumberFormatException ignored) {
                    // Report overflows the same way as other invalid IDs.
                }
            }
            throw new IllegalArgumentException("Expected a positive integer for " + key);
        }
    }

    public static final class Match {
        public final Request request;
        public final NativeDestination nativeDestination;

        Match(Request request, NativeDestination nativeDestination) {
            this.request = request;
            this.nativeDestination = nativeDestination;
        }
    }

    private static final class Entry {
        final String host;
        final String[] template;
        final Consumer<Request> validator;
        final NativeDestination destination;

        Entry(String host, String[] template, Consumer<Request> validator,
                NativeDestination destination) {
            this.host = host;
            this.template = template;
            this.validator = validator;
            this.destination = destination;
        }
    }

    private final List<Entry> entries = new ArrayList<>();

    public RouteTable flutter(String template, Consumer<Request> validator) {
        return register("flutter", template, validator, null);
    }

    public RouteTable nativePage(String template, Consumer<Request> validator,
            NativeDestination destination) {
        if (destination == null) throw new IllegalArgumentException("Native destination required");
        return register("native", template, validator, destination);
    }

    private RouteTable register(String host, String template, Consumer<Request> validator,
            NativeDestination destination) {
        String[] segments = segments(template);
        for (Entry entry : entries) {
            if (entry.host.equals(host) && overlaps(entry.template, segments)) {
                throw new IllegalArgumentException("Overlapping route template: " + template);
            }
        }
        entries.add(new Entry(host, segments, validator, destination));
        return this;
    }

    private static boolean variable(String segment) {
        return segment.startsWith("{") && segment.endsWith("}");
    }

    private static boolean overlaps(String[] first, String[] second) {
        if (first.length != second.length) return false;
        for (int i = 0; i < first.length; i++) {
            if (!variable(first[i]) && !variable(second[i]) && !first[i].equals(second[i])) {
                return false;
            }
        }
        return true;
    }

    public Match resolve(String url) {
        if (url == null) throw new IllegalArgumentException("A URL is required");
        URI uri = URI.create(url);
        String host = uri.getRawAuthority();
        if (!"samples".equals(uri.getScheme()) || uri.getRawFragment() != null
                || (!"native".equals(host) && !"flutter".equals(host))) {
            throw new IllegalArgumentException("Expected an in-app samples URL");
        }

        String[] path = segments(uri.getRawPath());
        for (int i = 0; i < path.length; i++) {
            String segment = decode(path[i], false);
            if (segment.contains("/") || segment.contains("\\")
                    || segment.equals(".") || segment.equals("..")) {
                throw new IllegalArgumentException("Invalid URL path segment");
            }
            path[i] = segment;
        }
        Map<String, String> query = query(uri.getRawQuery());
        for (Entry entry : entries) {
            if (!entry.host.equals(host) || entry.template.length != path.length) continue;
            Map<String, String> params = match(entry.template, path);
            if (params == null) continue;
            Request request = new Request(uri.toASCIIString(), params, query);
            entry.validator.accept(request);
            return new Match(request, entry.destination);
        }
        throw new UnknownRoute(url);
    }

    private static Map<String, String> match(String[] template, String[] path) {
        Map<String, String> params = new LinkedHashMap<>();
        for (int i = 0; i < template.length; i++) {
            String part = template[i];
            if (variable(part)) {
                params.put(part.substring(1, part.length() - 1), path[i]);
            } else if (!part.equals(path[i])) {
                return null;
            }
        }
        return params;
    }

    private static String[] segments(String path) {
        if (path == null || !path.startsWith("/") || path.endsWith("/") || path.contains("//")) {
            throw new IllegalArgumentException("Expected a non-empty URL path");
        }
        return path.substring(1).split("/");
    }

    private static Map<String, String> query(String raw) {
        Map<String, String> result = new LinkedHashMap<>();
        if (raw == null || raw.isEmpty()) return result;
        for (String pair : raw.split("&", -1)) {
            int separator = pair.indexOf('=');
            if (separator < 1) throw new IllegalArgumentException("Expected query key=value");
            String key = decode(pair.substring(0, separator), true);
            if (result.put(key, decode(pair.substring(separator + 1), true)) != null) {
                throw new IllegalArgumentException("Repeated query parameter: " + key);
            }
        }
        return result;
    }

    private static String decode(String raw, boolean query) {
        StringBuilder decoded = new StringBuilder();
        for (int index = 0; index < raw.length();) {
            char character = raw.charAt(index);
            if (character != '%') {
                decoded.append(query && character == '+' ? ' ' : character);
                index++;
                continue;
            }

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            while (index < raw.length() && raw.charAt(index) == '%') {
                // URI.create already checked the syntax of each percent escape.
                bytes.write(Integer.parseInt(raw.substring(index + 1, index + 3), 16));
                index += 3;
            }
            try {
                decoded.append(StandardCharsets.UTF_8.newDecoder().decode(ByteBuffer.wrap(bytes.toByteArray())));
            } catch (CharacterCodingException error) {
                throw new IllegalArgumentException("Invalid UTF-8 in the URL", error);
            }
        }
        return decoded.toString();
    }
}
