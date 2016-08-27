package com.kco.maze;

/**\
 *  
 * 记录迷宫图的坐标 
 * <功能详细描述> 
 *  
 * @author  c00212430 
 * @version  [版本号, 2013-11-27] 
 * @see  [相关类/方法] 
 * @since  [产品/模块版本] 
 */  
public class Point   
{   
  
    int x = 0;  
    int y = 0;  
  
    public Point() {  
     this(0, 0);  
    }  
  
    public Point(int x, int y) {  
     this.x = x;  
     this.y = y;  
    }  
  
    public boolean equals(Point p) {  
     return (x == p.x) && (y == p.y);  
    }  
      
  
    public int getX()  
    {  
        return x;  
    }  
  
    public void setX(int x)  
    {  
        this.x = x;  
    }  
  
    public int getY()  
    {  
        return y;  
    }  
  
    public void setY(int y)  
    {  
        this.y = y;  
    }  
  
    @Override  
    public String toString() {  
     return "(" + x + ", " + y + ")";  
    }  
   }  