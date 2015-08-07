package org.me.hotel

class HotelTest extends UnitTest("Hotel") {

  it should "forbid creating a Hotel with no rooms" in {
    Hotel() // works fine
    an [IllegalArgumentException] should be thrownBy {
      Hotel(rooms = List()) // oops...
    }
  }

  it should "forbid checking in if there are no free rooms" in {
  	val hotel = Hotel(List(Room(1).checkin(Guest("Victor"))))
    an [IllegalArgumentException] should be thrownBy {
    	hotel.checkin("Fish")
    }
  }

  it should "allow checking in" in {
    val busyRooms = Hotel()
      .checkin("Salvatore")
      .rooms.filter(room => !room.isFree())
    busyRooms should have size 1
    busyRooms.forall(_.guest == Option("Salvatore"))
  }

}