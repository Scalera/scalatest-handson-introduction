package org.me.hotel

class RoomTest extends UnitTest("Room") {
  
  it should "provide info about its occupation" in {
    Room(1).isFree() shouldEqual true
    Room(1,None).isFree() shouldEqual true
    Room(1,Some(Guest("Bruce"))).isFree() shouldEqual false
  }

  it should "allow registering a new guest if room is free" in {
    val occupiedRoom = Room(1).checkin(Guest("James"))
    occupiedRoom.isFree shouldEqual false
    occupiedRoom.guest shouldEqual(Option(Guest("James")))
  }

  it should "deny registering a new guest if room is already occupied" in {
    an [IllegalArgumentException] should be thrownBy {
      Room(1,Some(Guest("Barbara"))).checkin(Guest("Bruce"))
    }
  }

  it should "deny checking out if room is already free" in {
    an [IllegalArgumentException] should be thrownBy {
      Room(1).checkout()
    }
  }

  it should "allow checking out if room is occupied by someone" in {
    val room = Room(1,Some(Guest("Carmine")))
    val freeRoom = room.checkout()
    freeRoom.isFree shouldEqual true
  }

}