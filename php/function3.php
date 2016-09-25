<?php

// $extensions = get_loaded_extensions();
// foreach ($extensions as $key => $value){
// 	echo $key ." --> " . $value . "<br>";
// }

echo preg_replace_callback('~-([a-z])~', function ($match){
	return strtoupper($match[1]);
}, "hello-world\n");

$greet = function ($name){
	printf("Hello %s \r\n", $name);
};
$greet('World');
$greet("php");

$message = 'hello';
$example = function (){
	var_dump($message);
};
echo $example();

$example = function () use ($message){
	var_dump($message);
};
echo $example();

$message = 'world';
echo $example();

$message  =  'hello' ;
$example = function() use (& $message){
	var_dump($message);
};

echo $example();
$message = 'world';
echo $example();



$example = function ($arg) use ($message){
	var_dump($arg . ' ' . $message);
};

$example('hello');







?>