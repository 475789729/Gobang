package net.liuyao.core;

public class Step {
   public  byte[] position;
   //未来形成的局面分
   public float[] score;
   
   public Step(){
	   position = new byte[2];
	   score = new float[2];
   }
}
