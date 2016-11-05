package com.talestra.ktacme

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseWheelEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JScrollBar

abstract class JPoolScrollPanel<T : JComponent> : JPanel() {
	val panel = JPanel()
	val scrollBar = JScrollBar(JScrollBar.VERTICAL)
	val rows = arrayListOf<T>()
	open val rowPadding: Int = 16
	open val rowHeight: Int = 128
	var rowCount: Int = 0
		get() = field
		set(value) {
			field = value
			updateScrollBar()
		}

	fun updateScrollBar() {
		scrollBar.unitIncrement = rowHeight
		scrollBar.blockIncrement = height
		scrollBar.minimum = 0
		scrollBar.visibleAmount = height
		scrollBar.maximum = rowCount * rowHeight
	}

	fun updateRows() {
		//println(height)
		val expectedRows = Math.ceil(height.toDouble() / rowHeight.toDouble()) + 1


		while (rows.size < expectedRows) {
			val row = createRow()
			rows += row
			panel.add(row)
		}

		while (rows.size > expectedRows) {
			val item = rows.removeAt(rows.size - 1)
			panel.remove(item)
		}

		val value = scrollBar.value
		val offset = value / rowHeight

		for ((n, row) in rows.withIndex()) {
			val rowIndex = n + offset
			val y = rowHeight * n - value % rowHeight
			//println("$n")
			row.setBounds(0, y, width - scrollBar.width * 2, rowHeight - rowPadding)
			if (rowIndex >= 0 && rowIndex < rowCount) {
				row.isVisible = true
				updateRow(row, rowIndex)
			} else {
				row.isVisible = false
			}
		}
	}

	init {
		preferredSize = Dimension(512, 512)
		layout = BorderLayout(16, 0)
		add(panel.apply {
			layout = null
		}, BorderLayout.CENTER)
		add(scrollBar, BorderLayout.EAST)
		scrollBar.minimum = 0
		scrollBar.visibleAmount = 500
		scrollBar.maximum = 1000
		scrollBar.addAdjustmentListener { ev ->
			updateRows()
		}
		addComponentListener(object : ComponentAdapter() {
			override fun componentResized(e: ComponentEvent?) {
				updateRows()
				updateScrollBar()
			}
		})
		addMouseWheelListener { e ->
			//println(e)
			//e.scrollType
			when (e.scrollType) {
				MouseWheelEvent.WHEEL_UNIT_SCROLL -> scrollBar.value += e.scrollAmount * e.wheelRotation * scrollBar.unitIncrement / 4
				MouseWheelEvent.WHEEL_BLOCK_SCROLL -> scrollBar.value += e.scrollAmount * e.wheelRotation * scrollBar.blockIncrement / 4
			}
		}
		updateRows()
		rowCount = 10
	}

	abstract fun createRow(): T
	abstract fun updateRow(row: T, index: Int)
}