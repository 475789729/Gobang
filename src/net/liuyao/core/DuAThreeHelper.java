package net.liuyao.core;

public class DuAThreeHelper {
	private AI ai;
	private GameStatus gameStatus;
	public DuAThreeHelper(AI ai, GameStatus gameStatus){
		this.ai = ai;
		this.gameStatus = gameStatus;
	}
	
	//去掉不该堵的方向
	//color堵对方，对方活三，该color下棋
    public void filter(byte[][] maybe, int color, boolean vctAnaly){
    	if(isDuAThree(maybe, color)){
    		byte[] first;
    		byte[] end;
    		if(color == GameStatus.BLACK){
    			first =	maybe[maybe.length / 2];
    	    	 end = maybe[maybe.length / 2 + 1];
    		}else{
    			first = maybe[0];
        		end = maybe[1];
    		}
    	    	 byte[] morefirst = new byte[2];
    	    	 byte[] moreend = new byte[2];
    	    	 calcatePosition(first, end, morefirst, moreend);
    	    	 float dufirstScore;
    	    	 float duendScore;
    	    	 if(gameStatus.pointStatus[first[0]][first[1]] != GameStatus.EMPTY){
    	    		 throwWrong();
    	    	 }
    	    	 //假设堵first
    	    	 gameStatus.pointStatus[first[0]][first[1]] = (byte) color;
    	    	 dufirstScore = ai.weighValue.weighPoint(end, (byte)(0 - color))[0] + ai.weighValue.weighPoint(moreend, (byte)(0 - color))[0];
    	    	 //恢复
    	    	 gameStatus.pointStatus[first[0]][first[1]] = GameStatus.EMPTY;
    	    	 
    	    	 if(gameStatus.pointStatus[end[0]][end[1]] != GameStatus.EMPTY){
    	    		 throwWrong();
    	    	 }
    	    	//假设堵end
    	    	 gameStatus.pointStatus[end[0]][end[1]] = (byte) color;
    	    	 duendScore = ai.weighValue.weighPoint(first, (byte)(0 - color))[0] + ai.weighValue.weighPoint(morefirst, (byte)(0 - color))[0];
    	    	 //恢复
    	    	 gameStatus.pointStatus[end[0]][end[1]] = GameStatus.EMPTY;
    	    	 if(dufirstScore <= duendScore && duendScore > 10000f){
    	    		 //不考虑end
    	    		 end[0] = -1;
    	    		 end[1] = -1;
    	    		 return ;
    	    	 }else if(dufirstScore >= duendScore && dufirstScore > 10000f){
    	    		 first[0] = -1;
    	    		 first[1] = -1;
    	    		 return ;
    	    	 }
    	    	 if(vctAnaly){
    	    		//寻找vct
        	    	 boolean dufirstWillLose = false;
        	    	 boolean duendWillLose = false;
        	    	 //假设堵first
        	    	 ai.forward(new byte[]{first[0], first[1]}, (byte) color, AI.PREPOINT);
        	    	 ai.vctHelper.setGcolor((byte) (0 - color));
        			 VCTresult vctresult = ai.vctHelper.getVCT((0 - color), 1);
        			 if(vctresult.x == 1){
        				 dufirstWillLose = true;
        			 }
        	    	 ai.backward();
        	    	 //假设堵end
        	    	 ai.forward(new byte[]{end[0], end[1]}, (byte) color, AI.PREPOINT);
        	    	 ai.vctHelper.setGcolor((byte) (0 - color));
        	    	 vctresult = ai.vctHelper.getVCT((0 - color), 1);
        	    	 if(vctresult.x == 1){
        	    		 duendWillLose = true;
        			 }
        	    	 ai.backward();
        	    	 if(dufirstWillLose && (!duendWillLose)){
        	    		 first[0] = -1;
        	    		 first[1] = -1;
        	    		 return ;
        	    	 }else if((!dufirstWillLose) && duendWillLose){
        	    		 end[0] = -1;
        	    		 end[1] = -1;
        	    		 return ;
        	    	 }
    	    	 }
    	    	 //否则，按照哪头棋子数量的关系来决定
    	    	filterByCount(maybe, color);
    	    	 
    	    	 
    		
    	}
    }
    
    //堵防守力量弱的一边
    private void filterByCount(byte[][] maybe, int color){
    	if(isDuAThree(maybe, color)){
    		byte[] first;
    		byte[] end;
    		//防守力量，注意color是防守方
    		int firstFcount = 0;
    		int endFcount = 0;
    		//进攻力量
    		int firstGcount = 0;
    		int endGcount = 0;
    		if(color == GameStatus.BLACK){
    			first =	maybe[maybe.length / 2];
    	    	 end = maybe[maybe.length / 2 + 1];
    		}else{
    			first = maybe[0];
        		end = maybe[1];
    		}
    	    	 byte[] morefirst = new byte[2];
    	    	 byte[] moreend = new byte[2];
    	    	 calcatePosition(first, end, morefirst, moreend);
    	    	 for(byte i = gameStatus.left; i <= gameStatus.right; i++){
    				   for(byte j = gameStatus.top; j <= gameStatus.bottom; j++){
    					   if(gameStatus.pointStatus[i][j] == color){
    						   if(
    								   ((Math.abs(i - first[0]) <= 2 && Math.abs(j - first[1]) <= 2) || (Math.abs(i - morefirst[0]) <= 2 && Math.abs(j - morefirst[1]) <= 2))
    								   ){
    							   firstFcount++;
    						   }
    						   if(
    								   ((Math.abs(i - end[0]) <= 2 && Math.abs(j - end[1]) <= 2) || (Math.abs(i - moreend[0]) <= 2 && Math.abs(j - moreend[1]) <= 2))
    								   ){
    							   endFcount++;
    						   }
    					   }else if(gameStatus.pointStatus[i][j] == 0 - color){
    						   if(
    								   ((Math.abs(i - first[0]) <= 2 && Math.abs(j - first[1]) <= 2) || (Math.abs(i - morefirst[0]) <= 2 && Math.abs(j - morefirst[1]) <= 2))
    								   ){
    							   firstGcount++;
    						   }
    						   if(
    								   ((Math.abs(i - end[0]) <= 2 && Math.abs(j - end[1]) <= 2) || (Math.abs(i - moreend[0]) <= 2 && Math.abs(j - moreend[1]) <= 2))
    								   ){
    							   endGcount++;
    						   }
    					   }
    				   }
    				}
    	    	 
    	    	 if(firstFcount - firstGcount < endFcount - endGcount){
    	    		 //first防守力量薄弱，不考虑end
    	    		 end[0] = -1;
    	    		 end[1] = -1;
    	    		 return ;
    	    	 }else if(firstFcount - firstGcount > endFcount - endGcount){
    	    		 first[0] = -1;
    	    		 first[1] = -1;
    	    		 return ;
    	    	 }
    	}
    }
    
    
    public boolean isDuAThree(byte[][] maybe, int color){
    	if(color == GameStatus.BLACK){
    	 byte[] first =	maybe[maybe.length / 2];
    	 byte[] end = maybe[maybe.length / 2 + 1];
    	 if(first[0] == -1 || end[0] == -1){
    		 return false;
    	 }
    	 if(gameStatus.pointWhiteScore[first[0]][first[1]][1] == WeighValue.DEFAULT_AFOUR_LEVEL
    		&& gameStatus.pointWhiteScore[end[0]][end[1]][1] == WeighValue.DEFAULT_AFOUR_LEVEL
    			 ){
    		 if(
    				 (first[0] == end[0] && Math.abs(first[1] - end[1]) == 4)
    				 || (first[1] == end[1] && Math.abs(first[0] - end[0]) == 4)
    				 || (Math.abs(first[0] - end[0]) == 4 && Math.abs(first[1] - end[1]) == 4)
    				 ){
    			 byte[] morefirst = new byte[2];
    	    	 byte[] moreend = new byte[2];
    	    	 calcatePosition(first, end, morefirst, moreend);
    	    	 if(morefirst[0] >= 0 && morefirst[0] <= 14 && morefirst[1] >= 0 && morefirst[1] <= 14
    	    		&&	moreend[0] >= 0 && moreend[0] <= 14 && moreend[1] >= 0 && moreend[1] <= 14
    	    		&& gameStatus.pointStatus[morefirst[0]][morefirst[1]] == GameStatus.EMPTY
    	    		&& gameStatus.pointStatus[moreend[0]][moreend[1]] == GameStatus.EMPTY
    	    			 ){
    	    		 return true;
    	    	 }else{
    	    		 return false;
    	    	 }
    			 
    		 }else{
    			 return false;
    		 }
    		 
    	 }else{
    		 return false;
    	 }
    	}else{
    		byte[] first = maybe[0];
    		byte[] end = maybe[1];
    		if(first[0] == -1 || end[0] == -1){
       		 return false;
       	    }
    		if(gameStatus.pointBlackScore[first[0]][first[1]][1] == WeighValue.DEFAULT_AFOUR_LEVEL
    				&& gameStatus.pointBlackScore[end[0]][end[1]][1] == WeighValue.DEFAULT_AFOUR_LEVEL){
    			if(
       				 (first[0] == end[0] && Math.abs(first[1] - end[1]) == 4)
       				 || (first[1] == end[1] && Math.abs(first[0] - end[0]) == 4)
       				 || (Math.abs(first[0] - end[0]) == 4 && Math.abs(first[1] - end[1]) == 4)
       				 ){
    				
    				byte[] morefirst = new byte[2];
       	    	    byte[] moreend = new byte[2];
       	    	    calcatePosition(first, end, morefirst, moreend);
	       	    	 if(morefirst[0] >= 0 && morefirst[0] <= 14 && morefirst[1] >= 0 && morefirst[1] <= 14
	       	    		&&	moreend[0] >= 0 && moreend[0] <= 14 && moreend[1] >= 0 && moreend[1] <= 14
	       	    		&& gameStatus.pointStatus[morefirst[0]][morefirst[1]] == GameStatus.EMPTY
	       	    		&& gameStatus.pointStatus[moreend[0]][moreend[1]] == GameStatus.EMPTY
	       	    			 ){
	       	    		 return true;
	       	    	 }else{
	       	    		 return false;
	       	    	 }
       		 }else{
       			 return false;
       		 }
    			
    		}else{
    			return false;
    		}
    	}
    }
    
    private void calcatePosition(byte[] first, byte[] end, byte[] morefirst, byte[] moreend){
    	if(first[0] == end[0]){
    		//竖线
    		if(first[1] < end[1]){
    			morefirst[0] = first[0];
    			morefirst[1] = (byte) (first[1] - 1);
    			moreend[0] = end[0];
    			moreend[1] = (byte) (end[1] + 1);
    		}else{
    			morefirst[0] = first[0];
    			morefirst[1] = (byte) (first[1] + 1);
    			moreend[0] = end[0];
    			moreend[1] = (byte) (end[1] - 1);
    		}
    	}else if(first[1] == end[1]){
    		//横线
    		if(first[0] < end[0]){
    			morefirst[1] = first[1];
    			morefirst[0] = (byte) (first[0] - 1);
    			moreend[1] = end[1];
    			moreend[0] = (byte) (end[0] + 1);
    		}else{
    			morefirst[1] = first[1];
    			morefirst[0] = (byte) (first[0] + 1);
    			moreend[1] = end[1];
    			moreend[0] = (byte) (end[0] - 1);
    		}
    	}else if(first[0] < end[0] && first[1] < end[1]){
    		//leftxie
    		morefirst[0] = (byte) (first[0] - 1);
    		morefirst[1] = (byte) (first[1] - 1);
    		moreend[0] = (byte) (end[0] + 1);
    		moreend[1] = (byte) (end[1] + 1);
    	}else if(first[0] < end[0] && first[1] > end[1]){
    		//rightxie
    		morefirst[0] = (byte) (first[0] - 1);
    		morefirst[1] = (byte) (first[1] + 1);
    		moreend[0] = (byte) (end[0] + 1);
    		moreend[1] = (byte) (end[1] - 1);
    	}else if(first[0] > end[0] && first[1] > end[1]){
    		//leftxie
    		morefirst[0] = (byte) (first[0] + 1);
    		morefirst[1] = (byte) (first[1] + 1);
    		moreend[0] = (byte) (end[0] - 1);
    		moreend[1] = (byte) (end[1] - 1);
    	}else if(first[0] > end[0] && first[1] < end[1]){
    		//rightxie
    		morefirst[0] = (byte) (first[0] + 1);
    		morefirst[1] = (byte) (first[1] - 1);
    		moreend[0] = (byte) (end[0] - 1);
    		moreend[1] = (byte) (end[1] + 1);
    	}
    }
    
    
    private void throwWrong(){
    	int i = 1 / 0;
    }
}
