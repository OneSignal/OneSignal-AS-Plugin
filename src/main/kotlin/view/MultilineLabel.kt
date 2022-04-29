package view

import javax.swing.JTextArea
import javax.swing.UIManager

class MultilineLabel(text: String) : JTextArea(text) {

    init {
        background = null
        isEditable = false
        border = null
        lineWrap = true
        wrapStyleWord = true
        isOpaque = false
        font = UIManager.getFont("Label.font")
    }
}