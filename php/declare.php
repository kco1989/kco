<?php
	declare(ticks = 1);
	function tick_handler(){
		echo 'tick_handler() called<br/>';
	}
	
	register_tick_function('tick_handler');
	
	$a = 1;
	$a = 1;
// 	$a = 1;
// 	$a = 1;
// 	$a = 1;
// 	$a = 1;
// 	while($a > 0){
// 		if($a >= 100) break;
// 		$a += 2;
// 		print $a."<br/>";
// 	}
?>