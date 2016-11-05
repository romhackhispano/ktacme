package com.talestra.ktacme

import java.awt.event.MouseAdapter
import javax.swing.JFrame
import javax.swing.UIManager

fun main(args: Array<String>) {
	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
	val demo = Demo()
	demo.textRowsComponent.repository = TextRepository()
	demo.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
	demo.pack()
	demo.setLocationRelativeTo(null)
	demo.isVisible = true
	//com.talestra.ktacme/TextRowComponent
}