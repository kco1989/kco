<?php
$foo = include 'return1.php';

echo $foo."<br/>";

$bar = include 'noreturn.php';
echo $bar;

?>