package net.liuyao.core;

import java.util.Vector;

public class ScoreCache {
	//deepanalyze前进的缓存
	private int deepNow = -1;
	private float[][][][] blackScoreCache;
	private float[][][][] whiteScoreCache;
	private Vector<byte[]>[] rolePointCache;
	private Vector<byte[]>[] maybeRolePointCache;
	private GameStatus gameStatus;
	//prepoint前进的缓存
	private float[][][] currentBlackScore;
	private float[][][] currentWhiteScore;
	private Vector<byte[]> currentRolePoint;
	private Vector<byte[]> currentMaybeRolePoint;
   public  ScoreCache(GameStatus gameStatus){
	   blackScoreCache = new float[VCTHelper.MAX_DEEP][15][15][2];
	   whiteScoreCache = new float[VCTHelper.MAX_DEEP][15][15][2];
	   rolePointCache = new Vector[VCTHelper.MAX_DEEP];
	   maybeRolePointCache = new Vector[VCTHelper.MAX_DEEP];
	   for(int i = 0; i < rolePointCache.length; i++){
		   rolePointCache[i] = new Vector<byte[]>();
	   }
	   for(int i = 0; i < maybeRolePointCache.length; i++){
		   maybeRolePointCache[i] = new Vector<byte[]>();
	   }
	   this.gameStatus = gameStatus;
	   
	   currentBlackScore = new float[15][15][2];
	   currentWhiteScore = new float[15][15][2];
	   currentRolePoint = new Vector<byte[]>();
	   currentMaybeRolePoint = new Vector<byte[]>(); 
   }
   
   public void save(){
	   deepNow++;
	   for(int i = 0; i < 15; i++){
		   for(int j = 0; j < 15; j++){
			   blackScoreCache[deepNow][i][j][0] = gameStatus.pointBlackScore[i][j][0];
			   blackScoreCache[deepNow][i][j][1] = gameStatus.pointBlackScore[i][j][1];
			   whiteScoreCache[deepNow][i][j][0] = gameStatus.pointWhiteScore[i][j][0];
			   whiteScoreCache[deepNow][i][j][1] = gameStatus.pointWhiteScore[i][j][1];
		   }
	   }
	   Vector<byte[]> rolepointbackup = rolePointCache[deepNow];
	   rolepointbackup.clear();
	   for(int i = 0; i < gameStatus.rolePoint.size(); i++){
		   byte[] point = new byte[2];
		   point[0] = gameStatus.rolePoint.get(i)[0];
		   point[1] = gameStatus.rolePoint.get(i)[1];
		   rolepointbackup.add(point);
	   }
	   
	   Vector<byte[]> mayberolepointbackup = maybeRolePointCache[deepNow];
	   mayberolepointbackup.clear();
	   for(int i = 0; i < gameStatus.maybeRolePoint.size(); i++){
		   byte[] point = new byte[2];
		   point[0] = gameStatus.maybeRolePoint.get(i)[0];
		   point[1] = gameStatus.maybeRolePoint.get(i)[1];
		   mayberolepointbackup.add(point);
	   }
	   
   }
   
   public void restore(){
	   
	   for(int i = 0; i < 15; i++){
		   for(int j = 0; j < 15; j++){
			   gameStatus.pointBlackScore[i][j][0] = blackScoreCache[deepNow][i][j][0];
			   gameStatus.pointBlackScore[i][j][1] = blackScoreCache[deepNow][i][j][1];
			   gameStatus.pointWhiteScore[i][j][0] = whiteScoreCache[deepNow][i][j][0];
			   gameStatus.pointWhiteScore[i][j][1] = whiteScoreCache[deepNow][i][j][1];
		   }
	   }
	   
	   Vector<byte[]> rolepointbackup = rolePointCache[deepNow];
	   gameStatus.rolePoint.clear();
	   for(int i = 0; i < rolepointbackup.size(); i++){
		   byte[] point = new byte[2];
		   point[0] = rolepointbackup.get(i)[0];
		   point[1] = rolepointbackup.get(i)[1];
		   gameStatus.rolePoint.add(point);
	   }
	   
	   Vector<byte[]> mayberolepointbackup = maybeRolePointCache[deepNow];
	   gameStatus.maybeRolePoint.clear();
	   for(int i = 0; i < mayberolepointbackup.size(); i++){
		   byte[] point = new byte[2];
		   point[0] = mayberolepointbackup.get(i)[0];
		   point[1] = mayberolepointbackup.get(i)[1];
		   gameStatus.maybeRolePoint.add(point);
	   }
	   
	   deepNow--;
   }
   
   public void saveBeforePrepoint(){
	   for(int i = 0; i < 15; i++){
		   for(int j = 0; j < 15; j++){
			   currentBlackScore[i][j][0] = gameStatus.pointBlackScore[i][j][0];
			   currentBlackScore[i][j][1] = gameStatus.pointBlackScore[i][j][1];
			   currentWhiteScore[i][j][0] = gameStatus.pointWhiteScore[i][j][0];
			   currentWhiteScore[i][j][1] = gameStatus.pointWhiteScore[i][j][1];
		   }
	   }
	   
	   currentRolePoint.clear();
	   
	   for(int i = 0; i < gameStatus.rolePoint.size(); i++){
		   byte[] point = new byte[2];
		   point[0] = gameStatus.rolePoint.get(i)[0];
		   point[1] = gameStatus.rolePoint.get(i)[1];
		   currentRolePoint.add(point);
	   }
	   
	   currentMaybeRolePoint.clear();
	   for(int i = 0; i < gameStatus.maybeRolePoint.size(); i++){
		   byte[] point = new byte[2];
		   point[0] = gameStatus.maybeRolePoint.get(i)[0];
		   point[1] = gameStatus.maybeRolePoint.get(i)[1];
		   currentMaybeRolePoint.add(point);
	   }
	   
   }
   
   public void restoreAfterPrePoint(){
	   for(int i = 0; i < 15; i++){
		   for(int j = 0; j < 15; j++){
			   gameStatus.pointBlackScore[i][j][0] = currentBlackScore[i][j][0];
			   gameStatus.pointBlackScore[i][j][1] = currentBlackScore[i][j][1];
			   gameStatus.pointWhiteScore[i][j][0] = currentWhiteScore[i][j][0];
			   gameStatus.pointWhiteScore[i][j][1] = currentWhiteScore[i][j][1];
		   }
	   }
	   gameStatus.rolePoint.clear();
	   for(int i = 0; i < currentRolePoint.size(); i++){
		   byte[] point = new byte[2];
		   point[0] = currentRolePoint.get(i)[0];
		   point[1] = currentRolePoint.get(i)[1];
		   gameStatus.rolePoint.add(point);
	   }
	   
	   gameStatus.maybeRolePoint.clear();
	   for(int i = 0; i < currentMaybeRolePoint.size(); i++){
		   byte[] point = new byte[2];
		   point[0] = currentMaybeRolePoint.get(i)[0];
		   point[1] = currentMaybeRolePoint.get(i)[1];
		   gameStatus.maybeRolePoint.add(point);
	   }
   }
}
