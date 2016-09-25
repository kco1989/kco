<?php
function  takes_array ( $input )
{
	echo  " $input [ 0 ]  +  $input [ 1 ]  = " ,  $input [ 0 ]+ $input [ 1 ];
}

takes_array([2,2]);

function add_some_extra(& $string){
	$string .= "and something extra.";
}

$str = "This is a string, ";
add_some_extra($str);
echo $str."<br/>";

function makecoffee($type = "cappuccino"){
	return "Making a cup of $type .<br/>";
}

echo makecoffee();
echo makecoffee(null);
echo makecoffee("espresso");

function makecoffee2($type = array("cappuccino"),$coffeeMaker = NULL){
	$device = is_null($coffeeMaker) ? "hands" : $coffeeMaker;
	return "Making a cup of ". join(",",$type). " with $device .<br/>";
}

echo makecoffee2();
echo makecoffee2(array("cappuccino","lavazza"),"teapot");



function makeyogurt($type = "acidophilus", $flavour){
	return "Making a bowl of $type $flavour <br>";
}

echo makeyogurt("raspberry","good");