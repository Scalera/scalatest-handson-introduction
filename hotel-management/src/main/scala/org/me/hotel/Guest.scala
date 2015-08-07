package org.me.hotel

case class Guest(name: String) {
	require(!name.isEmpty,"Name must be defined")
}
