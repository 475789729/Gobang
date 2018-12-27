package net.liuyao.core;

public class ReChooseSavePoint {
	private GameStatus gameStatus;
	private AI ai;
	
   public ReChooseSavePoint(GameStatus gameStatus, AI ai){
	   this.gameStatus = gameStatus;
	   this.ai = ai;
   }
   
   
   
   
   //color是被堵方的颜色
   //lastX,lastY是被堵方的最后一步
   public byte[] reChooseSavePoint(int lastX, int lastY, int color){
	   int hengBegin = lastX;
		int hengEnd = lastX;
		int shuBegin = lastY;
		int shuEnd = lastY;
		int leftXieBegin = lastX;
		int leftXieEnd = lastX;
		int rightXieBegin = lastX;
		int rightXieEnd = lastX;
		
		//heng
		for(int t = lastX - 1;  t >= lastX - 4; t--){
			if(t == -1 || gameStatus.pointStatus[t][lastY] == 0 - color){
				hengBegin = t + 1;
				break;
			}else{
				hengBegin = t;
			}
		}
		for(int t = lastX + 1; t <= lastX + 4; t++){
			if(t == 15 || gameStatus.pointStatus[t][lastY] == 0 - color){
				hengEnd = t - 1;
				break;
			}else{
				hengEnd = t;
			}
		}
		//shu
		for(int t = lastY - 1; t >= lastY - 4; t--){
			if(t == -1 || gameStatus.pointStatus[lastX][t] == 0 - color){
				shuBegin = t + 1;
				break;
			}else{
				shuBegin = t;
			}
		}
		for(int t = lastY + 1; t <= lastY + 4; t++){
			if(t == 15 || gameStatus.pointStatus[lastX][t] == 0 - color){
				shuEnd = t - 1;
				break;
			}else{
				shuEnd = t;
			}
		}
		//rightXie
		for(int t = lastX - 1; t >= lastX - 4; t--){
			int z = lastY + lastX - t;
			if(t == -1 || z ==  15 || gameStatus.pointStatus[t][z] == 0 - color){
				rightXieBegin = t + 1;
				break;
			}else{
				rightXieBegin = t;
			}
		}
		for(int t = lastX + 1; t <= lastX + 4; t++){
			int z = lastY + lastX - t;
			if(t == 15 || z == -1 || gameStatus.pointStatus[t][z] == 0 - color){
				rightXieEnd = t - 1;
				break;
			}else{
				rightXieEnd = t;
			}
		}
		//leftXie
		for(int t = lastX - 1; t >= lastX - 4; t--){
			int z = lastY + t - lastX;
			if(t == -1 || z == -1 || gameStatus.pointStatus[t][z] == 0 - color){
				leftXieBegin = t + 1;
				break;
			}else{
				leftXieBegin = t;
			}
		}
		for(int t = lastX + 1; t <= lastX + 4; t++){
			int z = lastY + t - lastX;
			if(t == 15 || z == 15 || gameStatus.pointStatus[t][z] == 0 - color){
				leftXieEnd = t - 1;
				break;
			}else{
				leftXieEnd = t;
			}
		}
		
		float[][][] duPointScore = null;
		float[][][] beiduPointScore = null;
		if(color == GameStatus.BLACK){
			beiduPointScore = gameStatus.pointBlackScore;
			duPointScore = gameStatus.pointWhiteScore;
		}else{
			beiduPointScore = gameStatus.pointWhiteScore;
			duPointScore = gameStatus.pointBlackScore;
		}
		byte[] hengPoint = new byte[2];
		byte[] secondhengPoint = new byte[2];
		hengPoint[0] = -1;
		secondhengPoint[0] = -1;
		for(byte i = (byte)hengBegin; i <= hengEnd; i++){
			byte j = (byte) lastY;
			if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && duPointScore[i][j][1] != WeighValue.DEFAULT_MANY_LEVEL){
				if(hengPoint[0] == -1){
					hengPoint[0] = i;
					hengPoint[1] = j;
					
				}else{
					if(Math.abs(i - lastX) < Math.abs(hengPoint[0] - lastX)){
						hengPoint[0] = i;
						hengPoint[1] = j;
					}
				}
			}
		}
		if(hengPoint[0] != -1){
			for(byte i = (byte)hengBegin; i <= hengEnd; i++){
				byte j = (byte) lastY;
				if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && duPointScore[i][j][1] != WeighValue.DEFAULT_MANY_LEVEL && !(i == hengPoint[0] && j == hengPoint[1])){
					if(secondhengPoint[0] == -1){
						secondhengPoint[0] = i;
						secondhengPoint[1] = j;
						
					}else{
						if(Math.abs(i - lastX) < Math.abs(secondhengPoint[0] - lastX)){
							secondhengPoint[0] = i;
							secondhengPoint[1] = j;
						}
					}
				}
			}
		}
		
		
		byte[] shuPoint = new byte[2];
		byte[] secondshuPoint = new byte[2];
		shuPoint[0] = -1;
		secondshuPoint[0] = -1;
		for(byte j = (byte)shuBegin; j <= shuEnd; j++){
			byte i = (byte) lastX;
			if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && duPointScore[i][j][1] != WeighValue.DEFAULT_MANY_LEVEL){
				if(shuPoint[0] == -1){
					shuPoint[0] = i;
					shuPoint[1] = j;
				}else{
					if(Math.abs(j - lastY) < Math.abs(shuPoint[1] - lastY)){
						shuPoint[0] = i;
						shuPoint[1] = j;
					}
				}
			}
		}
		
		if(shuPoint[0] != -1){
			for(byte j = (byte)shuBegin; j <= shuEnd; j++){
				byte i = (byte) lastX;
				if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && duPointScore[i][j][1] != WeighValue.DEFAULT_MANY_LEVEL && !(i == shuPoint[0] && j == shuPoint[1])){
					if(secondshuPoint[0] == -1){
						secondshuPoint[0] = i;
						secondshuPoint[1] = j;
					}else{
						if(Math.abs(j - lastY) < Math.abs(secondshuPoint[1] - lastY)){
							secondshuPoint[0] = i;
							secondshuPoint[1] = j;
						}
					}
				}
			}
		}
		
		byte[] leftXiePoint = new byte[2];
		byte[] secondleftXiePoint = new byte[2];
		leftXiePoint[0] = -1;
		secondleftXiePoint[0] = -1;
		for(byte i = (byte)leftXieBegin; i <= leftXieEnd; i++){
			byte j = (byte) (lastY - lastX + i);
			if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && duPointScore[i][j][1] != WeighValue.DEFAULT_MANY_LEVEL){
				if(leftXiePoint[0] == -1){
					leftXiePoint[0] = i;
					leftXiePoint[1] = j;
				}else{
					if(Math.abs(i - lastX) < Math.abs(leftXiePoint[0] - lastX)){
						leftXiePoint[0] = i;
						leftXiePoint[1] = j;
					}
				}
			}
		}
		if(leftXiePoint[0] != -1){
			for(byte i = (byte)leftXieBegin; i <= leftXieEnd; i++){
				byte j = (byte) (lastY - lastX + i);
				if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && duPointScore[i][j][1] != WeighValue.DEFAULT_MANY_LEVEL && !(i == leftXiePoint[0] && j == leftXiePoint[1])){
					if(secondleftXiePoint[0] == -1){
						secondleftXiePoint[0] = i;
						secondleftXiePoint[1] = j;
					}else{
						if(Math.abs(i - lastX) < Math.abs(secondleftXiePoint[0] - lastX)){
							secondleftXiePoint[0] = i;
							secondleftXiePoint[1] = j;
						}
					}
				}
			}
		}
		byte[] rightXiePoint = new byte[2];
		byte[] secondrightXiePoint = new byte[2];
		rightXiePoint[0] = -1;
		secondrightXiePoint[0] = -1;
		for(byte i = (byte)rightXieBegin; i <= rightXieEnd; i++){
			byte j = (byte) (lastY + lastX - i);
			if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && duPointScore[i][j][1] != WeighValue.DEFAULT_MANY_LEVEL){
				if(rightXiePoint[0] == -1){
					rightXiePoint[0] = i;
					rightXiePoint[1] = j;
				}else{
					if(Math.abs(i - lastX) < Math.abs(rightXiePoint[0] - lastX)){
						rightXiePoint[0] = i;
						rightXiePoint[1] = j;
					}
				}
			}
		}
		if(rightXiePoint[0] != -1){
			for(byte i = (byte)rightXieBegin; i <= rightXieEnd; i++){
				byte j = (byte) (lastY + lastX - i);
				if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && duPointScore[i][j][1] != WeighValue.DEFAULT_MANY_LEVEL && !(i == rightXiePoint[0] && j == rightXiePoint[1])){
					if(secondrightXiePoint[0] == -1){
						secondrightXiePoint[0] = i;
						secondrightXiePoint[1] = j;
					}else{
						if(Math.abs(i - lastX) < Math.abs(secondrightXiePoint[0] - lastX)){
							secondrightXiePoint[0] = i;
							secondrightXiePoint[1] = j;
						}
					}
				}
			}
		}
	    if(hengPoint[0] != -1){
	    	ai.forward(new byte[]{hengPoint[0], hengPoint[1]}, (byte) (0 - color), AI.PREPOINT);
	   	    ai.vctHelper.setGcolor((byte) color);
			 VCTresult vctresult = ai.vctHelper.getVCT(color, 1);
			 ai.backward();
			 if(vctresult.x != 1){
				 return hengPoint;
			 }
	    }
		
   	    if(shuPoint[0] != -1){
   	    	ai.forward(new byte[]{shuPoint[0], shuPoint[1]}, (byte) (0 - color), AI.PREPOINT);
	   	    ai.vctHelper.setGcolor((byte) color);
	   	    VCTresult vctresult = ai.vctHelper.getVCT(color, 1);
			 ai.backward();
			 if(vctresult.x != 1){
				 return shuPoint;
			 }
   	    }
		 
		if(leftXiePoint[0] != -1){
			ai.forward(new byte[]{leftXiePoint[0], leftXiePoint[1]}, (byte) (0 - color), AI.PREPOINT);
	   	    ai.vctHelper.setGcolor((byte) color);
	   	     VCTresult vctresult = ai.vctHelper.getVCT(color, 1);
			 ai.backward();
			 if(vctresult.x != 1){
				 return leftXiePoint;
			 }
		}
			 
		if(rightXiePoint[0] != -1){
			ai.forward(new byte[]{rightXiePoint[0], rightXiePoint[1]}, (byte) (0 - color), AI.PREPOINT);
	   	    ai.vctHelper.setGcolor((byte) color);
	   	     VCTresult vctresult = ai.vctHelper.getVCT(color, 1);
			 ai.backward();
			 if(vctresult.x != 1){
				 return rightXiePoint;
			 }
		}
		
		if(secondhengPoint[0] != -1){
	    	ai.forward(new byte[]{secondhengPoint[0], secondhengPoint[1]}, (byte) (0 - color), AI.PREPOINT);
	   	    ai.vctHelper.setGcolor((byte) color);
			 VCTresult vctresult = ai.vctHelper.getVCT(color, 1);
			 ai.backward();
			 if(vctresult.x != 1){
				 return secondhengPoint;
			 }
	    }
		
		if(secondshuPoint[0] != -1){
   	    	ai.forward(new byte[]{secondshuPoint[0], secondshuPoint[1]}, (byte) (0 - color), AI.PREPOINT);
	   	    ai.vctHelper.setGcolor((byte) color);
	   	    VCTresult vctresult = ai.vctHelper.getVCT(color, 1);
			 ai.backward();
			 if(vctresult.x != 1){
				 return secondshuPoint;
			 }
   	    }
		
		if(secondleftXiePoint[0] != -1){
			ai.forward(new byte[]{secondleftXiePoint[0], secondleftXiePoint[1]}, (byte) (0 - color), AI.PREPOINT);
	   	    ai.vctHelper.setGcolor((byte) color);
	   	     VCTresult vctresult = ai.vctHelper.getVCT(color, 1);
			 ai.backward();
			 if(vctresult.x != 1){
				 return secondleftXiePoint;
			 }
		}
		
		if(secondrightXiePoint[0] != -1){
			ai.forward(new byte[]{secondrightXiePoint[0], secondrightXiePoint[1]}, (byte) (0 - color), AI.PREPOINT);
	   	    ai.vctHelper.setGcolor((byte) color);
	   	     VCTresult vctresult = ai.vctHelper.getVCT(color, 1);
			 ai.backward();
			 if(vctresult.x != 1){
				 return secondrightXiePoint;
			 }
		}
		
			return null;
   }
   
   
}
