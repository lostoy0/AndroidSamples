package com.lostoy.android.samples.widgets.input

import java.io.Serializable

class InputSampleData : Serializable {
    var type: Int = 0
    var name: String? = null
    var description: String? = null
    var subItems: MutableList<SubItem?>? = null

    class SubItem(var name: String?, var description: String?) : Serializable
}
