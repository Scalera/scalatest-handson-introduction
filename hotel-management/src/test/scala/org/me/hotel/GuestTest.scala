package org.me.hotel

class GuestTest extends UnitTest("Guest") {

  it should "have its name defined" in {
  	Guest("Seline") // works fine
    an [IllegalArgumentException] should be thrownBy {
    	Guest("") // oops...
    }
  }

}