<?php 
class  A  {
      public  $one  =  1 ;
    
      public function  show_one () {
          echo  $this -> one ;
      }
  }

  
//    $a  = new  A ;
//    $s  =  serialize ( $a );
//    // 把变量$s保存起来以便文件page2.php能够读到
//    file_put_contents ( 'store' ,  $s );


   $s1  =  file_get_contents ( 'store' );
   $a1  =  unserialize ( $s1 );

   // 现在可以使用对象$a里面的函数 show_one()
   $a1 -> show_one ();