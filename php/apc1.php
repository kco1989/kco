<?php
$array = [1,2];
$array1 = [3,4];
$array2 = array_merge($array,$array1);
$array[] = 5;
var_dump($array2);
var_dump($array);