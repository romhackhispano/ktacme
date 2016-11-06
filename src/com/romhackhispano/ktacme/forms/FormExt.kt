package com.romhackhispano.ktacme.forms

import java.awt.*
import javax.swing.*

inline fun Container.panel(callback: JPanel.() -> Unit): JPanel {
    val item = JPanel()
    add(item)
    item.callback()
    return item
}

inline fun Container.grid(rows: Int, columns: Int, callback: JPanel.() -> Unit): JPanel {
    val item = JPanel()
    val layout = GridLayout(rows, columns)
    item.layout = layout
    item.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)
    layout.hgap = 8
    layout.vgap = 8
    add(item, BorderLayout.WEST)
    item.callback()
    return item
}

inline fun Container.horizontalPanel(callback: JPanel.() -> Unit): JPanel {
    val item = JPanel()
    item.layout = BoxLayout(item, BoxLayout.X_AXIS)
    add(item, BorderLayout.WEST)
    item.callback()
    return item
}

inline fun Container.verticalPanel(callback: JPanel.() -> Unit): JPanel {
    val item = JPanel()
    item.layout = BoxLayout(item, BoxLayout.Y_AXIS)
    add(item, BorderLayout.WEST)
    item.callback()
    return item
}

inline fun Container.verticalPanelBL(callback: JPanel.() -> Unit): JPanel {
    val item = JPanel()
    item.layout = BorderLayout()
    add(item, BorderLayout.WEST)
    item.callback()
    return item
}

fun Container.button(text: String): JButton {
    val item = JButton(text)
    add(item, BorderLayout.WEST)
    return item
}

fun Container.horizontalGlue(): Component {
    val item = Box.createHorizontalGlue()
    add(item)
    return item
}

fun Container.verticalGlue(): Component {
    val item = Box.createVerticalGlue()
    add(item)
    return item
}

fun <T> Container.comboBox(): JComboBox<T> {
    val item = JComboBox<T>()
    add(item)
    return item
}

fun Container.label(text: String): JLabel {
    val item = JLabel(text)
    add(item, BorderLayout.WEST)
    return item
}

fun Container.textField(): JTextField {
    val item = JTextField()
    item.border = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.BLACK), BorderFactory.createEmptyBorder(4, 4, 4, 4))
    add(item, BorderLayout.WEST)
    return item
}

fun Container.textArea(): JTextArea {
    val item = JTextArea()
    add(item)
    return item
}

inline fun <T : AbstractButton> T.click(crossinline callback: () -> Unit): T {
    this.addActionListener {
        //println("****************")
        //println(it.actionCommand)
        callback()
    }
    return this
}

fun <T : JButton> T.makeDefault(): T {
    val rootPane = SwingUtilities.getRootPane(this)
    rootPane.defaultButton = this
    return this
}
