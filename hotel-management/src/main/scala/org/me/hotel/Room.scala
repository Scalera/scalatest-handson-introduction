package org.me.hotel

case class Room(number: Int, guest: Option[Guest] = None){ room =>

  def isFree(): Boolean =
    guest.isEmpty

  def checkin(guest: Guest): Room = {
    require(room.guest.isEmpty, "Room is occupied")
    Room(number,Some(guest))
  }

  def checkout(): Room = {
    require(guest.isDefined,"Room is already free")
    Room(number,None)
  }

}

