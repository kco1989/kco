<?php
function xrange($start, $limit, $step = 1){
	if($start < $limit){
		if ($step < 0){
			throw new LogicException('step must be +ve');
		}
		for ($i = $start; $i <= $limit; $i += $step){
			yield $i;
		}
	}else{
		if ($step >= 0){
			throw new LogicException('step must be -ve');
		}
		for ($i = $start; $i >= $limit; $i += $step){
			yield $i;
		}
	}
}

echo 'Single digit odd number form range(): ';
foreach (range(1, 9,2) as $number){
	echo "$number ";
}
echo "\n";

echo 'Single digit odd number form xrange():';
foreach (xrange(1, 9, 2) as $number){
	echo " $number ";
}
echo "\n";


$input = <<<'EOF'
1;PHP;Likes dorrar signs
2;Python;Likes whitespace
3;Ruby;Likes blocks
EOF;

function input_parser($input){
	foreach (explode("\n", $input) as $line){
		$fields = explode(";", $line);
		$id = array_shift($fields);
		yield $id => $fields;
	}
}

foreach (input_parser($input) as $id => $fields){
	echo "$id :\n";
	echo "	$fields[0] \n";
	echo "	$fields[1] \n";
}



