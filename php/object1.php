<?php
class SimpleClass{
	public $var = 'a default value';
	
	public function displasVar() {
		echo $this->var;
	}
}
class A{
	function foo() {
		if(isset($this)){
			echo '$this is defined (';
			echo get_class($this);
			echo ")\n";
		}else {
			echo "\$this is not defined.\n";
		}
	}
}
class B{
	function bar() {
		A::foo();
	}
}
$a = new A();
$a->foo();

A::foo();
$b = new B();
$b->bar();

B::bar();

function __autoload($name){
	echo "Want to load $name .\n";
	throw new Exception("Unable to load $name .");
}

try {
	$obj = new NonLoadableClass();
}catch (Exception $e){
	echo $e->getMessage(), "\n";
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
