<?php
foreach ($GLOBALS as $key => $value){
	echo "$key --> $value \n";
	if (is_array($value)){
		foreach ($value as $subkey => $subval){
			echo "\t $subkey --> $subval \n";
		}
	}
}