<?php
class BaseClass{
	function __construct() {
		print "In BaseClass contructor\n";
		$this->name = "hello";
	}
	
	function __destruct(){
		print "Destorying " .$this->name. "\n";
	}
}
class SubClass extends BaseClass{
	function __construct() {
		parent::__construct();
		print "in subclass constructor\n";
	}
}

class OtherSubClass extends BaseClass{
	public function OtherSubClass(){
		print "in OtherSubClass \n";
	}
}

$obj = new BaseClass();
$obj = new SubClass();
$obj = new OtherSubClass();