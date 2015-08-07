package org.me.hotel

case class Hotel(rooms: List[Room] = (1 to 10).map(n => Room(number=n)).toList){

	require(!rooms.isEmpty,"there are no rooms at all!")

  def checkin(personName: String): Hotel = {
    val (free,occupied) = rooms.partition(_.isFree)
    require(!free.isEmpty, "There are no free rooms")
    val (selectedRoom::restEmpty) = free
    Hotel(selectedRoom.checkin(Guest(personName)) +: (restEmpty ++ occupied))
  }

}

