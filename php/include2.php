<?php
function foo(){
	global $color;
	include 'vars.php';
	echo "A $color $fruit"."<br/>";
}

foo();
echo "AA $color $fruit"."<br/>";
?>