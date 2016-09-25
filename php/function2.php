<?php
function foo(){
	echo "In foo() <br/>";
}

function bar($arg = ''){
	echo "In bar(); argument was ' $arg ' .<br/>";
}

function echoit($string){
	echo $string;
}

$func = 'foo';
$func();

$func = 'bar';
$func('test');

$func = "echoit";
$func('test');

class Foo{
	function variable(){
		$name = 'Bar';
		$this -> $name();
	}
	function Bar() {
		echo "This is Bar<br>";
	}
}

$foo = new Foo();
$funcname = "variable";
$foo -> $funcname();


class  Bar
{
	static  $variable  =  'static property' ;
	static function  Variable ()
	{
		echo  'Method Variable called<br>' ;
	}
}
echo Bar :: $variable . "<br/>";

$variable = "Variable";
Bar :: $variable();
?>