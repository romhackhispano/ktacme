package com.talestra.ktacme

class TextRowsComponent : JPoolScrollPanel<TextRowComponent>() {
	var repository: TextRepository? = null
		set(value) {
			field = value
			rowCount = value!!.entries.size
			//scrollBar.value = 0
			updateRows()
		}

	override fun createRow(): TextRowComponent {
		return TextRowComponent()
	}

	override fun updateRow(row: TextRowComponent, index: Int) {
		row.entry = repository?.entries?.get(index) ?: TextRowComponent.TextEntry("ID", "ORIGINAL", "TRANSLATED")
	}
}