package net.liuyao.core;


import java.util.Vector;



public class AI {
	/**
	 * AI����Ըı�gameStatus����ΪҪ�����ܶ�㡣
	 * ��Ҫʵ��ǰ���ͺ��ˣ�ǰ������ģ�����壬�����޸�gameStatus���ݣ����˾��ǻָ�gameStatus��״̬
	 * ��Ҫ��̫��ķ����ܸı�gameStatus��ֻ��ǰ���ͺ����Լ�initGameStatus��ע��getBest��ӵظı�gameStatus
	 *  */
	private GameStatus gameStatus;
	public WeighValue weighValue;
	private int GO_NUM = 1;
	//ÿ����ɫ�ĺ�ѡ�������ܹ���2��
	private int rate = 3;
	private int grate = 3;
	private int frate = 3;
	private int deep = 4;
	private int gdeep = 3;
	private int fdeep = 4;
	private boolean role;
	//�������װ����vctǰ�������Լ�getbestǰ�����̵ĵ�
	public Vector<byte[]> forwardPoint;
	//�������װ�����ĵ㣬��ĳ�����壬�ȼ�����һ�����ٷ����������(���軹�Ǹ�����)����ô�ⲽ��������������棬������������forwardPoint����
	public Vector<byte[]> prePoint;
	
	public static final int REAL = 0;
	public static final int DEEPANALYZE = 1;
	public static final int PREPOINT = 2;
	
	public byte AIcolor;
	private boolean callStop = false;
	
	public VCTHelper vctHelper;
	private boolean useVCT = false;
	private ScoreCache scoreCache;
	private boolean carnivore;
	private DuAThreeHelper duAthreeHelper;
	private RobAThree robAThree;
	private CheckRolePoint checkRolePoint;
	private ReChooseSavePoint rechooseSavePoint;
	public  AI(GameStatus gameStatus, int grate, int frate, int gdeep, int fdeep, boolean role, byte AIcolor, boolean useVCT, boolean carnivore){
		this.gameStatus = gameStatus;
		this.grate = grate;
		this.frate = frate;
		this.gdeep = gdeep;
		this.fdeep = fdeep;
		this.role = role;
		this.carnivore = carnivore;
		this.AIcolor = AIcolor;
		weighValue = new WeighValue(gameStatus, role);
		forwardPoint = new Vector<byte[]>();
		prePoint = new Vector<byte[]>();
		initGameStatus();
		vctHelper = new VCTHelper(gameStatus, this);
		this.useVCT = useVCT;
		scoreCache = new ScoreCache(gameStatus);
		
		duAthreeHelper = new DuAThreeHelper(this, gameStatus);
		robAThree = new RobAThree(gameStatus, this);
		checkRolePoint = new CheckRolePoint(weighValue, gameStatus);
		rechooseSavePoint = new ReChooseSavePoint(gameStatus, this);
	}
	
	//��ʼ��ÿ��λ�õķ���
	public void initGameStatus(){
		for(byte i = 0; i < 15; i++){
			   for(byte j = 0; j < 15; j++){
				   float[] pointBlackResult = weighValue.weighPoint(new byte[]{i, j}, GameStatus.BLACK);
				   gameStatus.pointBlackScore[i][j][0] =  pointBlackResult[0];
				   gameStatus.pointBlackScore[i][j][1] =  pointBlackResult[1];
				   float[] pointWhiteResult = weighValue.weighPoint(new byte[]{i, j}, GameStatus.WHITE);
				   gameStatus.pointWhiteScore[i][j][0] =  pointWhiteResult[0];
				   gameStatus.pointWhiteScore[i][j][1] =  pointWhiteResult[1];
			   }
		   }
	}
	
	
	//����ÿ���ո񣬺�ѡ���˷�������ı�gameStatus
	//��������ѡ��ǰ��ֵ�ϸߵĿո�
	//��������Ծ��ǰ����ɱ��ת��������
	//maybeBlackGo��maybeWhiteGo�Ƕ���ģ��Ѿ���ʱ����vctȡ��
	//�п�ʱ���ع�����Ӧ��ɾ��maybeBlackGo��maybeWhiteGo
	public byte[][] chooseMaybe(int color){
		//maybeCheck.loadMax();
		byte[][] maybeBlack = new byte[rate][];
		float[][] maybeBlackScore = new float[rate][];
		byte[][] maybeBlackGo = new byte[GO_NUM][];
		for(int i = 0; i < maybeBlackGo.length; i++){
			maybeBlackGo[i] = new byte[2];
			maybeBlackGo[i][0] = -1;
		}
		for(int i = 0; i < rate; i++){
			maybeBlack[i] = new byte[2];
			maybeBlack[i][0] = -1;
			maybeBlackScore[i] = new float[2];
		}
		byte[][] maybeWhite = new byte[rate][];
		float[][] maybeWhiteScore = new float[rate][];
		byte[][] maybeWhiteGo = new byte[GO_NUM][];
		for(int i = 0; i < maybeWhiteGo.length; i++){
			maybeWhiteGo[i] = new byte[2];
			maybeWhiteGo[i][0] = -1;
		}
		for(int i = 0; i < rate; i++){
			maybeWhite[i] = new byte[2];
			maybeWhite[i][0] = -1;
			maybeWhiteScore[i] = new float[2];
		}
		
		int blackAnalyCount = 0;
		for(byte i = gameStatus.left; i <= gameStatus.right; i++){
			   for(byte j = gameStatus.top; j <= gameStatus.bottom; j++){
				  // if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && (gameStatus.pointBlackScore[i][j][0] > 10000f || maybeCheck.checkBlack(i, j))){
				   if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY){
				       blackAnalyCount++;
					   if(blackAnalyCount <= rate){
						   maybeBlack[blackAnalyCount - 1][0] = i;
						   maybeBlack[blackAnalyCount - 1][1] = j;
						   float[] pointResult = gameStatus.pointBlackScore[i][j];
						   maybeBlackScore[blackAnalyCount - 1][0] = pointResult[0];
						   maybeBlackScore[blackAnalyCount - 1][1] = pointResult[1];
					   
					   }
					   if(blackAnalyCount == rate){
						   //��һ������
						   for(int m = 0; m < rate - 1; m++){
							   for(int n = maybeBlackScore.length - 1;n - 1 >= m ;n--){
								   if(maybeBlackScore[n][0] > maybeBlackScore[n-1][0]){
									   float[] temp = maybeBlackScore[n-1];
									   maybeBlackScore[n-1] = maybeBlackScore[n];
									   maybeBlackScore[n] = temp;
									   byte[] positionTemp =  maybeBlack[n-1];
									   maybeBlack[n-1] = maybeBlack[n];
									   maybeBlack[n] = positionTemp;
								   }
							   }
						   }
						 
					   }else if(blackAnalyCount > rate){
						   if(gameStatus.pointBlackScore[i][j][0] > 10000f
								|| WeighValue.gfController != WeighValue.gfController_F
								
								   ){
							   float[] pointResult = gameStatus.pointBlackScore[i][j];
							   for(int m = maybeBlackScore.length - 1;m >= 0; m--){
								   if(maybeBlackScore[m][0] >= pointResult[0] && m == maybeBlackScore.length - 1){
									   break;
								   }else{
									   if(m == maybeBlackScore.length - 1){
										   
										   maybeBlackScore[m][0] = pointResult[0];
										   maybeBlackScore[m][1] = pointResult[1];	
										   maybeBlack[m][0] = i;
										   maybeBlack[m][1] = j;
									   }else{
										   if(maybeBlackScore[m][0] < maybeBlackScore[m + 1][0]){
											   float[] temp = maybeBlackScore[m];
											   maybeBlackScore[m] = maybeBlackScore[m + 1];
											   maybeBlackScore[m + 1] = temp;
											   byte[] positionTemp = maybeBlack[m];
											   maybeBlack[m] = maybeBlack[m + 1];
											   maybeBlack[m + 1] = positionTemp;
										   }else{
											   break;
										   }
									   }
								   }
							   }
						   }
						   
					   }
				   }
			   }
		   }
		
		
		//�ٷ�������
		int whiteAnalyCount = 0;
		for(byte i = gameStatus.left; i <= gameStatus.right; i++){
			   for(byte j = gameStatus.top; j <= gameStatus.bottom; j++){
				 //  if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && (gameStatus.pointWhiteScore[i][j][0] > 10000f || maybeCheck.checkWhite(i, j))){
				   if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY){
				       whiteAnalyCount++;
					   if(whiteAnalyCount <= rate){
						   maybeWhite[whiteAnalyCount - 1][0] = i;
						   maybeWhite[whiteAnalyCount - 1][1] = j;
						   float[] pointResult = gameStatus.pointWhiteScore[i][j];
						   maybeWhiteScore[whiteAnalyCount - 1][0] = pointResult[0];
						   maybeWhiteScore[whiteAnalyCount - 1][1] = pointResult[1];
					   
					   }
					   if(whiteAnalyCount == rate){
						   //��һ������
						   for(int m = 0; m < rate - 1; m++){
							   for(int n = maybeWhiteScore.length - 1;n - 1 >= m ;n--){
								   if(maybeWhiteScore[n][0] > maybeWhiteScore[n-1][0]){
									   float[] temp = maybeWhiteScore[n-1];
									   maybeWhiteScore[n-1] = maybeWhiteScore[n];
									   maybeWhiteScore[n] = temp;
									   byte[] positionTemp =  maybeWhite[n-1];
									   maybeWhite[n-1] = maybeWhite[n];
									   maybeWhite[n] = positionTemp;
								   }
							   }
						   }
					   }else if(whiteAnalyCount > rate){
						   //ֻ������ת��ɱ����������
						   if(gameStatus.pointWhiteScore[i][j][0] > 10000f || 
								   WeighValue.gfController != WeighValue.gfController_G
								   ){
							   float[] pointResult = gameStatus.pointWhiteScore[i][j];
							   for(int m = maybeWhiteScore.length - 1;m >= 0; m--){
								   if(maybeWhiteScore[m][0] >= pointResult[0] && m == maybeWhiteScore.length - 1){
									   break;
								   }else{
									   if(m == maybeWhiteScore.length - 1){
										   
										   maybeWhiteScore[m][0] = pointResult[0];
										   maybeWhiteScore[m][1] = pointResult[1];
										   maybeWhite[m][0] = i;
										   maybeWhite[m][1] = j;
									   }else{
										   if(maybeWhiteScore[m][0] < maybeWhiteScore[m + 1][0]){
											   float[] temp = maybeWhiteScore[m];
											   maybeWhiteScore[m] = maybeWhiteScore[m + 1];
											   maybeWhiteScore[m + 1] = temp;
											   byte[] positionTemp = maybeWhite[m];
											   maybeWhite[m] = maybeWhite[m + 1];
											   maybeWhite[m + 1] = positionTemp;
										   }else{
											   break;
										   }
									   }
								   }
							   }
						   }				   
					   }
				   }
			   }
		   }
		
		//ȥ��
		for(int go = 0; go < maybeBlackGo.length; go++){
			for(int i = 0; i < rate; i++){
				if(maybeBlackGo[go][0] == maybeBlack[i][0] && maybeBlackGo[go][1] == maybeBlack[i][1]){
					maybeBlackGo[go][0] = -1;
					maybeBlackGo[go][1] = -1;
					break;
				}
			}
		}
		
		for(int go = 0; go < maybeWhiteGo.length; go++){
			for(int i = 0; i < rate; i++){
				if(maybeWhiteGo[go][0] == maybeWhite[i][0] && maybeWhiteGo[go][1] == maybeWhite[i][1]){
					maybeWhiteGo[go][0] = -1;
					maybeWhiteGo[go][1] = -1;
					break;
				}
			}
		}
		
		
		byte[][] maybe= new byte[2 * rate + maybeBlackGo.length + maybeWhiteGo.length][];
		for(int i = 0; i < 2 * rate + maybeBlackGo.length + maybeWhiteGo.length; i++){
			maybe[i] = new byte[2];
			maybe[i][0] = -1;
			maybe[i][1] = -1;
		}
		//�������ã���ָ��һ���������㲻���ܱ�ѡ��
		for(int i = 0; i <  rate + maybeBlackGo.length; i++){
			if(i < rate){
				if(maybeBlack[i][0] != -1 && maybeBlack[i][1] != -1){
					maybe[i][0] = maybeBlack[i][0];
					maybe[i][1] = maybeBlack[i][1];
				}
				//�������5ָ�㣬����ĵ㲻����
				if(maybeBlackScore[i][0] > 1000000f){
					break;
				}
			}else{
				if(maybeBlackGo[i - rate][0] != -1 && maybeBlackGo[i - rate][1] != -1){
					maybe[i][0] = maybeBlackGo[i - rate][0];
					maybe[i][1] = maybeBlackGo[i - rate][1];
				}
			}
			
		}
		for(int i = rate + maybeBlackGo.length; i < 2 * rate + maybeWhiteGo.length + maybeBlackGo.length; i++){
			if(i < 2 * rate + maybeBlackGo.length){
				if(maybeWhite[i - rate - maybeBlackGo.length][0] != -1 && maybeWhite[i - rate - maybeBlackGo.length][1] != -1){
					maybe[i][0] = maybeWhite[i - rate - maybeBlackGo.length][0];
					maybe[i][1] = maybeWhite[i - rate - maybeBlackGo.length][1];
				}
				//�������5ָ�㣬����ĵ㲻����
				if(maybeWhiteScore[i - rate - maybeBlackGo.length][0] > 1000000f){
					break;
				}
			}else{
				if(maybeWhiteGo[i - 2 * rate - maybeBlackGo.length][0] != -1 && maybeWhiteGo[i - 2 * rate - maybeBlackGo.length][1] != -1){
					maybe[i][0] = maybeWhiteGo[i - 2 * rate - maybeBlackGo.length][0];
					maybe[i][1] = maybeWhiteGo[i - 2 * rate - maybeBlackGo.length][1];
				}
			}
			
		}
		
		//���ٰ���Ŀ���
		if(WeighValue.gfController == WeighValue.gfController_G){
			for(int i = rate + maybeBlackGo.length; i < 2 * rate +  maybeBlackGo.length; i++){
				if(maybeWhiteScore[i - rate - maybeBlackGo.length][0] < 10000f){
					maybe[i][0] = -1;
					maybe[i][1] = -1;
				}
			}
		}
		
		//���ٺ���Ŀ���
		if(WeighValue.gfController == WeighValue.gfController_F){
			for(int i = 0; i < rate; i++){
				if(maybeBlackScore[i][0] < 10000f){
					maybe[i][0] = -1;
					maybe[i][1] = -1;
				}
			}
		}
		
		
		//�ٴμ���
		if(color == GameStatus.BLACK){
			//��������
			if(maybeBlackScore[0][0] > 1000000f){
				//���ӵ�
				for(int i = 0; i < rate + maybeWhiteGo.length; i++){
					maybe[rate + maybeBlackGo.length + i][0] = -1;
					maybe[rate + maybeBlackGo.length + i][1] = -1;
					
					if(i > 0){
						maybe[i][0] = -1;
						maybe[i][1] = -1;
					}
				}
			}else if(maybeWhiteScore[0][0] > 1000000f){
				//���ӵ�
				for(int i = 0; i < rate + maybeWhiteGo.length; i++){
					maybe[i][0] = -1;
					maybe[i][1] = -1;
					if(i > 0){
						maybe[rate + maybeBlackGo.length + i][0] = -1;
						maybe[rate + maybeBlackGo.length + i][1] = -1;
					}
				}
			}else if(maybeBlackScore[0][0] > 100000f){
				//ע���ļ��ľ�ɱ�㣬�п���Ŀǰ�ǻ������γɣ����Լ���ֻ�����κ�һ�����򣬷���ȴ��Ҫ������������
				for(int i = 0; i < rate + maybeWhiteGo.length; i++){
					maybe[rate + maybeBlackGo.length + i][0] = -1;
					maybe[rate + maybeBlackGo.length + i][1] = -1;
					if(i > 0){
						maybe[i][0] = -1;
						maybe[i][1] = -1;
					}
					
				}
				
			}else if(maybeWhiteScore[0][0] > 100000f){
				//ע���ļ��ľ�ɱ�㣬�п���Ŀǰ�ǻ������γɣ����Լ���ֻ�����κ�һ�����򣬷���ȴ��Ҫ������������
				for(int i = 0; i < rate; i++){
					if(maybeBlackScore[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[i][0] = -1;
						maybe[i][1] = -1;
					}	
					if(maybeWhiteScore[i][0] < 100000f){
						maybe[i + rate + maybeBlackGo.length][0] = -1;
						maybe[i + rate + maybeBlackGo.length][1] = -1;
					}
				}
				for(int i = 0; i < maybeBlackGo.length; i++){
					if(maybeBlackGo[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[rate + i][0] = -1;
						maybe[rate + i][1] = -1;
					}
				}
				for(int i = 0; i < maybeWhiteGo.length; i++){
					maybe[i + rate * 2 + maybeBlackGo.length][0] = -1;
					maybe[i + rate * 2 + maybeBlackGo.length][1] = -1;
				}
			}else if(maybeBlackScore[0][0] > 10000f){
				for(int i = 0; i < rate; i++){
					if(maybeBlackScore[i][0] > 10000f || maybeBlackScore[i][1] == WeighValue.DEFAULT_DFOUR_LEVEL){
						
					}else{
						maybe[i][0] = -1;
						maybe[i][1] = -1;
					}
					
					if(maybeWhiteScore[i][1] == WeighValue.DEFAULT_DFOUR_LEVEL){
						
					}else{
						maybe[rate + maybeBlackGo.length + i][0] = -1;
						maybe[rate + maybeBlackGo.length + i][1] = -1;
					}
				}
				for(int i = 0; i < maybeBlackGo.length; i++){
					if(maybeBlackGo[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[rate + i][0] = -1;
						maybe[rate + i][1] = -1;
					}
				}
				for(int i = 0; i < maybeWhiteGo.length; i++){
					if(maybeWhiteGo[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[2 * rate + maybeBlackGo.length + i][0] = -1;
						maybe[2 * rate + maybeBlackGo.length + i][1] = -1;
					}
				}
			}else if(maybeWhiteScore[0][0] > 10000f){
				for(int i = 0; i < rate; i++){
					if(maybeBlackScore[i][1] == WeighValue.DEFAULT_DFOUR_LEVEL || maybeBlackScore[i][1] == WeighValue.DEFAULT_THREE_LEVEL){
						
					}else{
						maybe[i][0] = -1;
						maybe[i][1] = -1;
					}
					
					if(maybeWhiteScore[i][0] > 10000f || maybeWhiteScore[i][1] == WeighValue.DEFAULT_DFOUR_LEVEL){
						
					}else{
						maybe[rate + maybeBlackGo.length + i][0] = -1;
						maybe[rate + maybeBlackGo.length + i][1] = -1;
					}
				}
				for(int i = 0; i < maybeWhiteGo.length; i++){
					if(maybeWhiteGo[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[2 * rate + maybeBlackGo.length + i][0] = -1;
						maybe[2 * rate + maybeBlackGo.length + i][1] = -1;
					}
				}
			}
			
		}else{
			//��������
			if(maybeWhiteScore[0][0] > 1000000f){
				//���ӵ�
				for(int i = 0; i < rate + maybeBlackGo.length; i++){
					maybe[i][0] = -1;
					maybe[i][1] = -1;
					if(i > 0){
						maybe[i + rate + maybeBlackGo.length][0] = -1;
						maybe[i + rate + maybeBlackGo.length][1] = -1;
					}
				}
			}else if(maybeBlackScore[0][0] > 1000000f){
				//���ӵ�
				for(int i = 0; i < rate + maybeBlackGo.length; i++){
					maybe[rate + maybeBlackGo.length + i][0] = -1;
					maybe[rate + maybeBlackGo.length + i][1] = -1;
					if(i > 0){
						maybe[i][0] = -1;
						maybe[i][1] = -1;
					}
				}
			}else if(maybeWhiteScore[0][0] > 100000f){
				//ע���ļ��ľ�ɱ�㣬�п���Ŀǰ�ǻ������γɣ����Լ���ֻ�����κ�һ�����򣬷���ȴ��Ҫ������������
				for(int i = 0; i < rate + maybeBlackGo.length; i++){
					maybe[i][0] = -1;
					maybe[i][1] = -1;
					if(i > 0){
						maybe[i + rate + maybeBlackGo.length][0] = -1;
						maybe[i + rate + maybeBlackGo.length][1] = -1;
					}
				}
			}else if(maybeBlackScore[0][0] > 100000f){
				//ע���ļ��ľ�ɱ�㣬�п���Ŀǰ�ǻ������γɣ����Լ���ֻ�����κ�һ�����򣬷���ȴ��Ҫ������������
				for(int i = 0; i < rate; i++){
					if(maybeWhiteScore[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[i + rate + maybeBlackGo.length][0] = -1;
						maybe[i + rate + maybeBlackGo.length][1] = -1;
					}
					if(maybeBlackScore[i][0] < 100000f){
						maybe[i][0] = -1;
						maybe[i][1] = -1;
					}
				}
				for(int i = 0; i < maybeBlackGo.length; i++){
					maybe[rate + i][0] = -1;
					maybe[rate + i][1] = -1;
				}
				for(int i = 0; i < maybeWhiteGo.length; i++){
					if(maybeWhiteGo[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[2 * rate + maybeBlackGo.length + i][0] = -1;
						maybe[2 * rate + maybeBlackGo.length + i][1] = -1;
					}
				}
			}else if(maybeWhiteScore[0][0] > 10000f){
				for(int i = 0; i < rate; i++){
					if(maybeWhiteScore[i][0] > 10000f || maybeWhiteScore[i][1] == WeighValue.DEFAULT_DFOUR_LEVEL){
						
					}else{
						maybe[i + rate + maybeBlackGo.length][0] = -1;
						maybe[i + rate + maybeBlackGo.length][1] = -1;
					}
					
					if(maybeBlackScore[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[i][0] = -1;
						maybe[i][1] = -1;
					}
				}
				
				for(int i = 0; i < maybeWhiteGo.length; i++){
					if(maybeWhiteGo[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[2 * rate + maybeBlackGo.length + i][0] = -1;
						maybe[2 * rate + maybeBlackGo.length + i][1] = -1;
					}
				}
				for(int i = 0; i < maybeBlackGo.length; i++){
					if(maybeBlackGo[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[rate + i][0] = -1;
						maybe[rate + i][1] = -1;
					}
				}
			}else if(maybeBlackScore[0][0] > 10000f){
				for(int i = 0; i < rate; i++){
					if(maybeWhiteScore[i][1] == WeighValue.DEFAULT_DFOUR_LEVEL || maybeWhiteScore[i][1] == WeighValue.DEFAULT_THREE_LEVEL){
						
					}else{
						maybe[i + rate + maybeBlackGo.length][0] = -1;
						maybe[i + rate + maybeBlackGo.length][1] = -1;
					}
					
					if(maybeBlackScore[i][0] > 10000f || maybeBlackScore[i][1] == WeighValue.DEFAULT_DFOUR_LEVEL){
						
					}else{
						maybe[i][0] = -1;
						maybe[i][1] = -1;
					}
				}
				for(int i = 0; i < maybeBlackGo.length; i++){
					if(maybeBlackGo[i][1] != WeighValue.DEFAULT_DFOUR_LEVEL){
						maybe[rate + i][0] = -1;
						maybe[rate + i][1] = -1;
					}
				}
			}
			
		}
		
		
		return maybe;
	}
	
	//�ɵ����ϵת��Ϊ�������ϵ��������ϵ�ķ�ֵ��Ҫֱ�ӱȽ�
	//������ǰ�����
	//�������һ�������ľ�ɱ�����ģ�����Ϊ���ƾ���
	//turn����һ������ɫ
	public float[] getCurrentSituationScore(byte color, byte turn){
		//���ֺ͸���
		float[] result = new float[2];
		
		float maxBlack = 0f;
		float maxWhite = 0f;
		
		 //����������
		    //�������������ܶµĹ����з�չңԶ��˫����
		    int athreeBlackCount = 0;
		    //ңԶ������
		    int dfourBlackCount = 0;
		    //�վ�ɱ��
		    int doublethreeEmptyBlackCount = 0;
		    int doublefourEmptyBlackCount = 0;
			for(int i = gameStatus.left; i <= gameStatus.right; i++){
				for(int j = gameStatus.top; j <= gameStatus.bottom; j++){
					if(gameStatus.pointStatus[i][j] == GameStatus.BLACK){
						if(gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL){
							if(color == GameStatus.BLACK){
								result[0] = 2000000f;
								return result;
							}else{
								result[0] = 2000000f;
								return result;
							}
							
						}else if(gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_DFOUR_LEVEL && turn == GameStatus.BLACK){
							if(200000f > maxBlack){
								maxBlack = 200000f;
								continue;
							}
						}else if(gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_THREE_LEVEL && turn == GameStatus.BLACK){
							if(20000f > maxBlack){
								maxBlack = 20000f;
								continue;
							}
						}
						
						if(gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_THREE_LEVEL){
							athreeBlackCount++;
						}
						if(gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_DFOUR_LEVEL){
							dfourBlackCount++;
						}
						if(gameStatus.pointBlackScore[i][j][0] > maxBlack){
							maxBlack = gameStatus.pointBlackScore[i][j][0];
						}
					}else if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY){
						if(turn == GameStatus.BLACK){
							if(gameStatus.pointBlackScore[i][j][0] > 100000f){
								if(maxBlack < 100000f){
									maxBlack = 100000f;
								}
								
							}else if(gameStatus.pointBlackScore[i][j][0] > 10000f){
								if(maxBlack < 10000f){
									maxBlack = 10000f;
								}
							}
						}else{
							//�ų�afour
							if(gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_DOUBLEFOUR
									|| gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_THREEFOUR){
								doublefourEmptyBlackCount++;
								
							}else if(gameStatus.pointBlackScore[i][j][1] == WeighValue.DEFAULT_DOUBLETHREE){
								doublethreeEmptyBlackCount++;
							}
						}
						
					}
					
				}
			}
			
			//�µĹ��̲���ңԶ��˫����
			if(athreeBlackCount >= 6){
				if(20000f > maxBlack){
					maxBlack = 20000f;
					
				}
			}else if(athreeBlackCount >= 3 && doublefourEmptyBlackCount + doublethreeEmptyBlackCount > 0){
				if(20000f > maxBlack){
					maxBlack = 20000f;
					
				}
			}else if(doublefourEmptyBlackCount > 0 && doublethreeEmptyBlackCount > 0){
				if(20000f > maxBlack){
					maxBlack = 20000f;				
				}
			}
			//ңԶ����
			if(athreeBlackCount >= 3 && dfourBlackCount >= 4){
				if(200000f > maxBlack){
					maxBlack = 200000f;
				}
			}else if(dfourBlackCount >= 4 && doublefourEmptyBlackCount + doublethreeEmptyBlackCount > 0){
				if(200000f > maxBlack){
					maxBlack = 200000f;
				}
			}
			//����������
			int athreeWhiteCount = 0;
			int dfourWhiteCount = 0;
			//�վ�ɱ��
		    int doublethreeEmptyWhiteCount = 0;
		    int doublefourEmptyWhiteCount = 0;
			for(int i = gameStatus.left; i <= gameStatus.right; i++){
				for(int j = gameStatus.top; j <= gameStatus.bottom; j++){
					if(gameStatus.pointStatus[i][j] == GameStatus.WHITE){
						if(gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_FIVE_LEVEL){
							if(color == GameStatus.BLACK){
								result[0] = -2000000f;
								return result;
							}else{
								result[0] = 2000000f;
								return result;
							}
							
						}else if(gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_DFOUR_LEVEL && turn == GameStatus.WHITE){
							if(200000f > maxWhite){
								maxWhite = 200000f;
								continue;
							}
						}else if(gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_THREE_LEVEL && turn == GameStatus.WHITE){
							if(20000f > maxWhite){
								maxWhite = 20000f;
								continue;
							}
						}
						if(gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_THREE_LEVEL){
							athreeWhiteCount++;
						}
						if(gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_DFOUR_LEVEL){
							dfourWhiteCount++;
						}
						if(gameStatus.pointWhiteScore[i][j][0] > maxWhite){
							maxWhite = gameStatus.pointWhiteScore[i][j][0];
						}
					}else if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY){
						if(turn == GameStatus.WHITE){
							if(gameStatus.pointWhiteScore[i][j][0] > 100000f){
								if(maxWhite < 100000f){
									maxWhite = 100000f;
								}
							}else if(gameStatus.pointWhiteScore[i][j][0] > 10000f){
								if(maxWhite < 10000f){
									maxWhite = 10000f;
								}
							}
						}else{
							//�ų�afour
							if(gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_DOUBLEFOUR
									|| gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_THREEFOUR){
								doublefourEmptyWhiteCount++;
							}else if(gameStatus.pointWhiteScore[i][j][1] == WeighValue.DEFAULT_DOUBLETHREE){
								doublethreeEmptyWhiteCount++;
							}
						}
						
					}
				}
			}
			//�µĹ����в���ңԶ��˫����
			if(athreeWhiteCount >= 6){
				if(20000f > maxWhite){
					maxWhite = 20000f;
				}
			}else if(athreeWhiteCount >= 3 && doublefourEmptyWhiteCount + doublethreeEmptyWhiteCount > 0){
				if(20000f > maxWhite){	
					maxWhite = 20000f;
				}
			}else if(doublefourEmptyWhiteCount > 0 && doublethreeEmptyWhiteCount > 0){
				if(20000f > maxWhite){	
					maxWhite = 20000f;
				}
			}
			if(athreeWhiteCount >= 3 && dfourWhiteCount >= 4){
				if(200000f > maxWhite){
					maxWhite = 200000f;
				}
			}else if(dfourWhiteCount >= 4 && doublefourEmptyWhiteCount + doublethreeEmptyWhiteCount > 0){
				if(200000f > maxWhite){
					maxWhite = 200000f;
				}
			}
			
		//�ж�˭�ľ�ɱ����
		if(maxBlack >= 10000f && maxWhite >= 10000f){
			if(color == GameStatus.BLACK){
				
				if(maxBlack > maxWhite){
					result[0] = maxBlack;
					return result;
				}else if(maxBlack == maxWhite){
					if(turn == GameStatus.BLACK){
						result[0] = maxBlack;
						return result;
					}else{
						result[0] = -maxWhite;
						return result;
					}
				}else{
					result[0] = -maxWhite;
					return result;
				}
			}else{
				if(maxWhite > maxBlack){
					result[0] = maxWhite;
					return result;
					
				}else if(maxBlack == maxWhite){
					if(turn == GameStatus.WHITE){
						result[0] = maxWhite;
						return result;
					}else{
						result[0] = -maxBlack;
						return result;
					}
				}else{
					result[0] = -maxBlack;
					return result;
				}
			}
		}
		
		
		
		if(color == GameStatus.BLACK){
			//��������
			      result[0] = maxBlack * WeighValue.gfController - (1 - WeighValue.gfController) * maxWhite;
			      result[1] = -maxWhite;
			      return  result;
				
			
			
		}else{
			//��������
			    result[0] = (1 - WeighValue.gfController) * maxWhite - maxBlack * WeighValue.gfController;
				result[1] = -maxBlack;
				return result;
				
		}
	}
	
	public Step getBest(){
		//���漸�д��룬���Է��Ƿ��ģ�֮������Ҫ�⼸�д��룬����ΪVCTHelperΪ�����ܣ�������ط���һ��ֻ�ܷ��ء���bug�����Ǵ�bug�޺�
		boolean needVctCheck = true;
		boolean duishouWillFive = false;
		boolean meHighScorePoint = false;
		float[][][] myScore = null;
		float[][][] duishouScore = null;
		if(AIcolor == GameStatus.BLACK){
			myScore = gameStatus.pointBlackScore;
			duishouScore = gameStatus.pointWhiteScore;
		}else{
			myScore = gameStatus.pointWhiteScore;
			duishouScore = gameStatus.pointBlackScore;
		}
		for(int i = gameStatus.left; i <= gameStatus.right; i++){
			for(int j = gameStatus.top; j <= gameStatus.bottom; j++){
				if(gameStatus.pointStatus[i][j] == GameStatus.EMPTY && myScore[i][j][0] > 100000f){
					meHighScorePoint = true;
				}
			}
		}
		byte[] lastHistoryPoint = gameStatus.historyPoint.get(gameStatus.historyPoint.size() - 1);
		if(duishouScore[lastHistoryPoint[0]][lastHistoryPoint[1]][1] == WeighValue.DEFAULT_AFOUR_LEVEL
				|| duishouScore[lastHistoryPoint[0]][lastHistoryPoint[1]][1] == WeighValue.DEFAULT_DFOUR_LEVEL
				|| duishouScore[lastHistoryPoint[0]][lastHistoryPoint[1]][1] == WeighValue.DEFAULT_DOUBLEFOUR
				|| duishouScore[lastHistoryPoint[0]][lastHistoryPoint[1]][1] == WeighValue.DEFAULT_THREEFOUR){
			duishouWillFive = true;
		}
		if(meHighScorePoint || duishouWillFive){
			needVctCheck = false;
		}
		//bug������ϣ����濪ʼ������
		
		boolean findPlayerVCT = false;
		
		if(needVctCheck && useVCT && gameStatus.historyPoint.size() > 5 && gameStatus.black_DoubleFour == -1 && gameStatus.white_DoubleFour == -1){
			
			//�ȷ���ai�Լ���vct
			vctHelper.setGcolor(this.AIcolor);
			VCTresult vctresult = vctHelper.getVCT(AIcolor, 1);
			if(vctresult.x == 1){
				
				vctHelper.AIvctRecoder = gameStatus.historyPoint.size() + 1;
				Step vct = new Step();
				vct.position = vctresult.position;
				return vct;		
			}
			//����player��vct������ִ�ײ���Ҫ�������ֵ�vct��һֱ�ڷ���
		
				vctHelper.setGcolor((byte) (0 - this.AIcolor));
				vctresult = vctHelper.getVCT((byte) (0 - this.AIcolor), 1);
				if(vctresult.x == 1){
					
					findPlayerVCT = true;
						
				}
			
			
		}
		
		if(findPlayerVCT || (!needVctCheck)){
			if(duishouWillFive){
				WeighValue.turnF();
				
				
			}else if(meHighScorePoint){
				WeighValue.turnG();
			}else{
				WeighValue.turnF();
				if(carnivore){
					return reChooseSavePoint();
				}
				
			}
			
		}else{
			if(useVCT){
				byte[] robAThreePoint = robAThree.getRobATHreePoint();
				if(robAThreePoint[0] != -1){
					Step robAThreeStep = new Step();
					robAThreeStep.position = robAThreePoint;
					return robAThreeStep;
				}
			}
			
			if(carnivore && useVCT && gameStatus.historyPoint.size() > 5 && gameStatus.black_DoubleFour == -1 && gameStatus.white_DoubleFour == -1){
				if(AIcolor == GameStatus.BLACK){
					turnG();
					Step preVCT = getPreVCT(AIcolor);
					if(preVCT == null){
						return null;
					}
						
					if(preVCT.position[0] != -1){
						turnG();
						Step step = getBest(AIcolor, deep, true);
						return step;
					}
					
						turnF();
						preVCT = getPreVCT((byte) (0 - AIcolor));
						if(preVCT == null){
							return null;
						}
						if(preVCT.position[0] != -1){
							turnF();
							Step step = getBest(AIcolor, deep, true);
							return step;
						}
					
					
				}else{
					turnF();
					Step preVCT = getPreVCT((byte) (0 - AIcolor));
					if(preVCT == null){
						return null;
					}
					if(preVCT.position[0] != -1){
						turnF();
						Step step = getBest(AIcolor, deep, true);
						return step;
					}
					turnG();
					preVCT = getPreVCT(AIcolor);
					if(preVCT == null){
						return null;
					}
						
					if(preVCT.position[0] != -1){
						turnG();
						Step step = getBest(AIcolor, deep, true);
						return step;
					}
				}
				
			}
			
			if(AIcolor == GameStatus.BLACK){
				turnG();
			}else{
				if(carnivore && gameStatus.historyPoint.size() > 16){
					turnG();
				}else{
					turnF();
				}
				
			}
			
		}
		Step step = getBest(AIcolor, deep, true);
		return step;
	}
	private void turnG(){
		WeighValue.turnG();
		this.deep = gdeep;
		this.rate = grate;
	}
	
	private void turnF(){
		WeighValue.turnF();
		this.deep = fdeep;
		this.rate = frate;
	}
	private boolean isG(byte color){
		if(color == GameStatus.BLACK && WeighValue.gfController == WeighValue.gfController_G){
			return true;
		}else if(color == GameStatus.WHITE && WeighValue.gfController == WeighValue.gfController_F){
			return true;
		}else{
			return false;
		}
	}
	
	private Step reChooseSavePoint(){
		WeighValue.turnF();
		Step step = getBest(AIcolor, deep, true);
		if(step == null){
			return null;
		}
		forward(new byte[]{step.position[0], step.position[1]}, AIcolor, PREPOINT);
		vctHelper.setGcolor((byte) (0 - this.AIcolor));
		VCTresult vctresult = vctHelper.getVCT((byte) (0 - this.AIcolor), 1);
		backward();
        if(vctresult.x == 1){
			byte[] lastPoint = gameStatus.historyPoint.get(gameStatus.historyPoint.size() - 1);
			
			byte[] rechoose = rechooseSavePoint.reChooseSavePoint(lastPoint[0], lastPoint[1], 0 - AIcolor);
			if(rechoose != null){
				
				step.position = rechoose;
				return step;
			}else{
				return step;
			}
		}else{
			return step;
		}
	}
	//���þ������ϵ���������ϵ��Ҫ�͵����ϵ�����Ƚ�
	//��deep�������������
	//colorΪ����
	//nowRoot�Ǳ�ʾ�Ƿ�������㣬��û�еݹ����getBest
	public Step getBest(byte color, int deep, boolean nowRoot){
		
		//�û�����˻��壬���̷���
		if(callStop){
			
			return null;
		}
		
		if(deep == 1){
			float[] situationScore = new float[2];
			situationScore[0] = - 30000000f;
			situationScore[1] = - 30000000f;
			Step best = new Step();	
			byte[][] maybe = chooseMaybe(color);
			
			if(isG(color)){
				if(robAThree.filterGMaybe(maybe, color)){
					
				}else{
					maybe = chooseMaybe(color);
				}
			}
			if(color == AIcolor && nowRoot){
				duAthreeHelper.filter(maybe, color, true);
			}
			if(color == AIcolor && (!nowRoot)){
				duAthreeHelper.filter(maybe, color, false);
			}
			
			for(int i= 0; i < maybe.length; i++){
				if(maybe[i][0] != -1){
					forward(maybe[i], color, DEEPANALYZE);				
					float[] thisScore = getCurrentSituationScore(color,(byte)(0 - color));
					if(thisScore[0] > situationScore[0]){
						situationScore[0] = thisScore[0];
						situationScore[1] = thisScore[1];
						best.score[0] = thisScore[0];
						best.score[1] = thisScore[1];
						best.position[0] = maybe[i][0];
						best.position[1] = maybe[i][1];
					}else if(thisScore[0] == situationScore[0] && thisScore[1] > situationScore[1]){
						situationScore[0] = thisScore[0];
						situationScore[1] = thisScore[1];
						best.score[0] = thisScore[0];
						best.score[1] = thisScore[1];
						best.position[0] = maybe[i][0];
						best.position[1] = maybe[i][1];
					}
					backward();
					
				}
			}
			//�û�����˻��壬���̷���
			if(callStop){
				
				return null;
			}
			
			return best;
		}else{
			boolean fiveExist = false;
			float[] minNext = new float[2];
			minNext[0] = 3000000f;
			minNext[1] = 3000000f;
			Step best = new Step();
			byte[][] maybe = chooseMaybe(color);
			
			if(nowRoot && isG(color)){
				if(robAThree.filterGMaybe(maybe, color)){
					
				}else{
					maybe = chooseMaybe(color);
				}
			}
			
			if(color == AIcolor && nowRoot){
				duAthreeHelper.filter(maybe, color, true);
			}
			if(color == AIcolor && (!nowRoot)){
				duAthreeHelper.filter(maybe, color, false);
			}
			
			for(int i = 0; i < maybe.length; i++){
				if(maybe[i][0] != -1){
					forward(maybe[i], color, DEEPANALYZE);
					if(gameStatus.winner == color){
						fiveExist = true;
						best.score[0] = 2000000f;			
						best.position[0] = maybe[i][0];
						best.position[1] = maybe[i][1];
					}else if(gameStatus.winner == 0 - color){
						//�����µ��˽��ֵ�
						if(2000000f < minNext[0]){
							minNext[0] = 2000000f;
							best.position[0] = maybe[i][0];
							best.position[1] = maybe[i][1];
							best.score[0] = -2000000f;	
						}
					}else{
						Step next = getBest((byte)(0 - color), deep - 1, false);					
						//�û�����˻��壬���̷���
						if(next == null){
							return null;
						}
						//next�ķ�����վ�ڵ������������
						if(next.score[0] < minNext[0]){
							minNext[0] = next.score[0];
							minNext[1] = next.score[1];
							best.position[0] = maybe[i][0];
							best.position[1] = maybe[i][1];
							best.score[0] = -minNext[0];
							best.score[1] = -minNext[1];
						}else if(next.score[0] == minNext[0] && next.score[1] < minNext[1]){
							minNext[0] = next.score[0];
							minNext[1] = next.score[1];
							best.position[0] = maybe[i][0];
							best.position[1] = maybe[i][1];
							best.score[0] = -minNext[0];
							best.score[1] = -minNext[1];
						}
					}
					backward();
					if(fiveExist){
						return best;
					}
				}
				
			}
			return best;
		}
				
		
	}
	
	//AIִ��ʱ����ô˷���
	//ʹ�ù�����ɸѡ������ǣ����
  public Step getPreVCT(byte color){
		
		//�û�����˻��壬���̷���
		if(callStop){
			
			return null;
		}
		
		
			float[][][] pointScore = null;
			if(color == GameStatus.BLACK){
				pointScore = gameStatus.pointBlackScore;
			}else{
				pointScore = gameStatus.pointWhiteScore;
			}
			Step best = new Step();
			best.position[0] = -1;
			best.position[1] = -1;
			byte[][] maybe = chooseMaybe(color);
			int beginIndex = 0;
			int length = maybe.length / 2;
			if(color == GameStatus.BLACK){
				beginIndex = 0;
			}else{
				beginIndex = length;
			}
			for(int i= 0; i < length; i++){
				if(maybe[i + beginIndex][0] != -1 && pointScore[maybe[i + beginIndex][0]][maybe[i + beginIndex][1]][1] == WeighValue.DEFAULT_LOW_LEVEL){
					forward(maybe[i + beginIndex], color, PREPOINT);				
					vctHelper.setGcolor(color);
					VCTresult vctresult = vctHelper.getVCT(color, 1);
					if(vctresult.x == 1){
						byte[] robAThreePoint = robAThree.getRobATHreePoint();
						if(robAThreePoint[0] == -1){
							best.position[0] = maybe[i + beginIndex][0];
							best.position[1] = maybe[i + beginIndex][1];
							backward();
							return best;
						}
								
					}
					
					backward();
					
					//�û�����˻��壬���̷���
					if(callStop){
						
						return null;
					}
				}
			}
			//�û�����˻��壬���̷���
			if(callStop){
				
				return null;
			}
			
			return best;
		
				
		
	}
	
	
	//������������ʾ������ⲽ����������
	public void forward(byte[] position,byte color, int type){
		if(gameStatus.pointStatus[position[0]][position[1]] != GameStatus.EMPTY){
			
			throwWrong();
		}
		if(type == REAL){
			gameStatus.historyPoint.add(position);
			
		}else if(type == PREPOINT){
			scoreCache.saveBeforePrepoint();
			prePoint.add(position);
		}
		else{
			scoreCache.save();
			forwardPoint.add(position);
		}
		
		gameStatus.pointStatus[position[0]][position[1]] = color;
		gameStatus.getGameController().adjustRectangle(position);
		updateJuMian(position, GameStatus.BLACK, role);
		updateJuMian(position, GameStatus.WHITE, role);
		if(color == GameStatus.BLACK){
			if(gameStatus.pointBlackScore[position[0]][position[1]][1] == WeighValue.DEFAULT_FIVE_LEVEL
					
					){
				   if(gameStatus.winner == GameStatus.EMPTY)
					   gameStatus.winner = GameStatus.BLACK;
			}else if(gameStatus.pointBlackScore[position[0]][position[1]][1] == WeighValue.DEFAULT_AFOUR_LEVEL
					|| gameStatus.pointBlackScore[position[0]][position[1]][1] == WeighValue.DEFAULT_DOUBLEFOUR
					|| gameStatus.pointBlackScore[position[0]][position[1]][1] == WeighValue.DEFAULT_THREEFOUR
					){
				if(gameStatus.black_DoubleFour == -1){
					gameStatus.black_DoubleFour = gameStatus.historyPoint.size() + forwardPoint.size();
				}
			}else if(gameStatus.pointBlackScore[position[0]][position[1]][1] == WeighValue.DEFAULT_DOUBLETHREE){
				if(gameStatus.black_DoubleThree == -1){
					gameStatus.black_DoubleThree = gameStatus.historyPoint.size() + forwardPoint.size();
				}
			}else if(role && gameStatus.pointBlackScore[position[0]][position[1]][1] == WeighValue.DEFAULT_MANY_LEVEL){
				if(gameStatus.winner == GameStatus.EMPTY)
					   gameStatus.winner = GameStatus.WHITE;
			}
		}else{
			if(gameStatus.pointWhiteScore[position[0]][position[1]][1] == WeighValue.DEFAULT_FIVE_LEVEL
					
					){
				   if(gameStatus.winner == GameStatus.EMPTY)
					   gameStatus.winner = GameStatus.WHITE;
			}else if(gameStatus.pointWhiteScore[position[0]][position[1]][1] == WeighValue.DEFAULT_AFOUR_LEVEL
					|| gameStatus.pointWhiteScore[position[0]][position[1]][1] == WeighValue.DEFAULT_DOUBLEFOUR
					|| gameStatus.pointWhiteScore[position[0]][position[1]][1] == WeighValue.DEFAULT_THREEFOUR){
				if(gameStatus.white_DoubleFour == -1){
					gameStatus.white_DoubleFour = gameStatus.historyPoint.size() + forwardPoint.size();
				}
			}else if(gameStatus.pointWhiteScore[position[0]][position[1]][1] == WeighValue.DEFAULT_DOUBLETHREE){
				if(gameStatus.white_DoubleThree == -1){
					gameStatus.white_DoubleThree = gameStatus.historyPoint.size() + forwardPoint.size();
				}
			}
		}
		
		if(gdeep < 4 && gameStatus.historyPoint.size() % 10 == 0 && gameStatus.historyPoint.size() > 8 && gameStatus.historyPoint.size() < 60 && type == REAL){
			grate++;
		}
		if(gdeep > 3 && gameStatus.historyPoint.size() % 10 == 0 && gameStatus.historyPoint.size() > 8 && gameStatus.historyPoint.size() < 29 && type == REAL){
			grate++;
		}
		if(fdeep < 4 && gameStatus.historyPoint.size() % 10 == 0 && gameStatus.historyPoint.size() > 8 && gameStatus.historyPoint.size() < 60 && type == REAL){
			frate++;
		}
		if(fdeep > 3 && gameStatus.historyPoint.size() % 10 == 0 && gameStatus.historyPoint.size() > 8 && gameStatus.historyPoint.size() < 29 && type == REAL){
			frate++;
		}
	}
	//����
	public void backward(){
		int type = -1;
		gameStatus.winner = GameStatus.EMPTY;
		
		byte[] last = null;
		if(forwardPoint.size() > 0){
			type = DEEPANALYZE;
			last = forwardPoint.get(forwardPoint.size() - 1);
			forwardPoint.remove(forwardPoint.size() - 1);
		}else if(prePoint.size() > 0){
			type = PREPOINT;
			last = prePoint.get(prePoint.size() - 1);
			prePoint.remove(prePoint.size() - 1);
		}
		else{
			type = REAL;
			last = gameStatus.historyPoint.get(gameStatus.historyPoint.size() - 1);
			gameStatus.historyPoint.remove(gameStatus.historyPoint.size() - 1);
			//ai�ڵڼ����³���vct
			if(gameStatus.historyPoint.size() < vctHelper.AIvctRecoder){
				vctHelper.AIvctRecoder = -1;
			}
			if(gameStatus.historyPoint.size() % 10 == 9 && gameStatus.historyPoint.size() > 8 && type == REAL){
				grate--;
				frate--;
			}
		}
		
		if(gameStatus.historyPoint.size() + forwardPoint.size() < gameStatus.black_DoubleThree){
			gameStatus.black_DoubleThree = -1;
		}
		if(gameStatus.historyPoint.size() + forwardPoint.size() < gameStatus.black_DoubleFour){
			gameStatus.black_DoubleFour = -1;
		}
		if(gameStatus.historyPoint.size() + forwardPoint.size() < gameStatus.white_DoubleThree){
			gameStatus.white_DoubleThree = -1;
		}
		if(gameStatus.historyPoint.size() + forwardPoint.size() < gameStatus.white_DoubleFour){
			gameStatus.white_DoubleFour = -1;
		}
		
		gameStatus.pointStatus[last[0]][last[1]] = GameStatus.EMPTY;
		gameStatus.getGameController().adjustRectangle();
		if(type == REAL){
			updateJuMian(last, GameStatus.BLACK, role);
			updateJuMian(last, GameStatus.WHITE, role);
		}else if(type == PREPOINT){
			scoreCache.restoreAfterPrePoint();
		}
		else{
			scoreCache.restore();
		}
		
	}
	
	public void stopThink(){
		callStop = false;
		while(forwardPoint.size() > 0){
			backward();
		}
	}
	//���¾���
	//ά��ÿ����ķ�ֵ
	//ά�����ֵ㣬˫����
	public void updateJuMian(byte[] position, byte color, boolean role){
		if(role && color == GameStatus.BLACK){
			collectMaybeRolePointAndRolePointAndUpdateScore(position);
		}else{
			updateScore(position, color, role);
		}
	}
	 //���¼���õ㼰����Χ��ķ�ֵ
	public void updateScore(byte[] position, byte color, boolean role){
		
		
		
		int hengBegin = position[0];
		int hengEnd = position[0];
		int shuBegin = position[1];
		int shuEnd = position[1];
		int leftXieBegin = position[0];
		int leftXieEnd = position[0];
		int rightXieBegin = position[0];
		int rightXieEnd = position[0];
		
		//heng
		for(int i = position[0] - 1;  i >= position[0] - 4; i--){
			if(i == -1 || gameStatus.pointStatus[i][position[1]] == 0 - color){
				hengBegin = i + 1;
				break;
			}else{
				hengBegin = i;
			}
		}
		for(int i = position[0] + 1; i <= position[0] + 4; i++){
			if(i == 15 || gameStatus.pointStatus[i][position[1]] == 0 - color){
				hengEnd = i - 1;
				break;
			}else{
				hengEnd = i;
			}
		}
		//shu
		for(int j = position[1] - 1; j >= position[1] - 4; j--){
			if(j == -1 || gameStatus.pointStatus[position[0]][j] == 0 - color){
				shuBegin = j + 1;
				break;
			}else{
				shuBegin = j;
			}
		}
		for(int j = position[1] + 1; j <= position[1] + 4; j++){
			if(j == 15 || gameStatus.pointStatus[position[0]][j] == 0 - color){
				shuEnd = j - 1;
				break;
			}else{
				shuEnd = j;
			}
		}
		//rightXie
		for(int i = position[0] - 1; i >= position[0] - 4; i--){
			int j = position[1] + position[0] - i;
			if(i == -1 || j ==  15 || gameStatus.pointStatus[i][j] == 0 - color){
				rightXieBegin = i + 1;
				break;
			}else{
				rightXieBegin = i;
			}
		}
		for(int i = position[0] + 1; i <= position[0] + 4; i++){
			int j = position[1] + position[0] - i;
			if(i == 15 || j == -1 || gameStatus.pointStatus[i][j] == 0 - color){
				rightXieEnd = i - 1;
				break;
			}else{
				rightXieEnd = i;
			}
		}
		//leftXie
		for(int i = position[0] - 1; i >= position[0] - 4; i--){
			int j = position[1] + i - position[0];
			if(i == -1 || j == -1 || gameStatus.pointStatus[i][j] == 0 - color){
				leftXieBegin = i + 1;
				break;
			}else{
				leftXieBegin = i;
			}
		}
		for(int i = position[0] + 1; i <= position[0] + 4; i++){
			int j = position[1] + i - position[0];
			if(i == 15 || j == 15 || gameStatus.pointStatus[i][j] == 0 - color){
				leftXieEnd = i - 1;
				break;
			}else{
				leftXieEnd = i;
			}
		}
		float[][][] pointScore = null;
		if(color == GameStatus.BLACK){
			pointScore = gameStatus.pointBlackScore;
		}
		else{
			pointScore = gameStatus.pointWhiteScore;
		}
			
		for(byte i = (byte)hengBegin; i <= hengEnd; i++){
			byte j = position[1];
			//ֻ�к�Ҫ����i,j
			if(gameStatus.pointStatus[i][j] != 0 - color && (!role || color == GameStatus.WHITE || (!isRolePoint(i, j)))){
				float[] result = weighValue.weighPoint(new byte[]{i,j}, color);
				if(role && color == GameStatus.BLACK && result[1] == WeighValue.DEFAULT_DOUBLETHREE){
					//�ٽ���
					result[0] = WeighValue.SCORE_ATHREE + WeighValue.SCORE_DTHREE;
				    result[1] = WeighValue.DEFAULT_THREE_LEVEL;
				}
				pointScore[i][j] = result;
				
			}
			
		}
		for(byte j = (byte)shuBegin; j <= shuEnd; j++){
			byte i = position[0];
			if(gameStatus.pointStatus[i][j] != 0 - color && !(i == position[0] && j == position[1]) && (!role || color == GameStatus.WHITE || (!isRolePoint(i, j)))){
			  float[] result = weighValue.weighPoint(new byte[]{i,j}, color);
			  if(role && color == GameStatus.BLACK && result[1] == WeighValue.DEFAULT_DOUBLETHREE){
				//�ٽ���
				    result[0] = WeighValue.SCORE_ATHREE + WeighValue.SCORE_DTHREE;
				    result[1] = WeighValue.DEFAULT_THREE_LEVEL;
				}
			   pointScore[i][j] = result;
			   
			}
		}
		for(byte i = (byte)leftXieBegin; i <= leftXieEnd; i++){
			byte j = (byte) (position[1] - position[0] + i);
			if(gameStatus.pointStatus[i][j] != 0 - color && !(i == position[0] && j == position[1]) && (!role || color == GameStatus.WHITE || (!isRolePoint(i, j)))){
			  float[] result = weighValue.weighPoint(new byte[]{i,j}, color);
			  if(role && color == GameStatus.BLACK && result[1] == WeighValue.DEFAULT_DOUBLETHREE){
				//�ٽ���
				    result[0] = WeighValue.SCORE_ATHREE + WeighValue.SCORE_DTHREE;
				    result[1] = WeighValue.DEFAULT_THREE_LEVEL;
				}
			  pointScore[i][j] = result;
			  
			}
		}
		for(byte i = (byte)rightXieBegin; i <= rightXieEnd; i++){
			byte j = (byte) (position[1] + position[0] - i);
			if(gameStatus.pointStatus[i][j] != 0 - color && !(i == position[0] && j == position[1]) && (!role || color == GameStatus.WHITE || (!isRolePoint(i, j)))){
			  float[] result = weighValue.weighPoint(new byte[]{i,j}, color);
			  if(role && color == GameStatus.BLACK && result[1] == WeighValue.DEFAULT_DOUBLETHREE){
					//�ٽ���
				    result[0] = WeighValue.SCORE_ATHREE + WeighValue.SCORE_DTHREE;
				    result[1] = WeighValue.DEFAULT_THREE_LEVEL;
				}
			  pointScore[i][j] = result;
			  
			}
		}
		
		
	}
	//�ؼ�˼����˫�����ֺ��������ַֿ����ж�
	public void collectMaybeRolePointAndRolePointAndUpdateScore(byte[] position){
		//ȥ�����ֵ��е�˫����һ��������ж����е�˫����
		for(int i = 0; i < gameStatus.rolePoint.size(); i++){
			if(maybeRolePointContains(gameStatus.rolePoint.get(i)[0], gameStatus.rolePoint.get(i)[1])){
				gameStatus.rolePoint.remove(i);
				i--;
			}
		}
		
		int hengBegin = position[0];
		int hengEnd = position[0];
		int shuBegin = position[1];
		int shuEnd = position[1];
		int leftXieBegin = position[0];
		int leftXieEnd = position[0];
		int rightXieBegin = position[0];
		int rightXieEnd = position[0];
		
		//heng
		for(int i = position[0] - 1;  i >= position[0] - 4; i--){
			if(i == -1 || gameStatus.pointStatus[i][position[1]] == GameStatus.WHITE){
				hengBegin = i + 1;
				break;
			}else{
				hengBegin = i;
			}
		}
		for(int i = position[0] + 1; i <= position[0] + 4; i++){
			if(i == 15 || gameStatus.pointStatus[i][position[1]] == GameStatus.WHITE){
				hengEnd = i - 1;
				break;
			}else{
				hengEnd = i;
			}
		}
		//shu
		for(int j = position[1] - 1; j >= position[1] - 4; j--){
			if(j == -1 || gameStatus.pointStatus[position[0]][j] == GameStatus.WHITE){
				shuBegin = j + 1;
				break;
			}else{
				shuBegin = j;
			}
		}
		for(int j = position[1] + 1; j <= position[1] + 4; j++){
			if(j == 15 || gameStatus.pointStatus[position[0]][j] == GameStatus.WHITE){
				shuEnd = j - 1;
				break;
			}else{
				shuEnd = j;
			}
		}
		//rightXie
		for(int i = position[0] - 1; i >= position[0] - 4; i--){
			int j = position[1] + position[0] - i;
			if(i == -1 || j ==  15 || gameStatus.pointStatus[i][j] == GameStatus.WHITE){
				rightXieBegin = i + 1;
				break;
			}else{
				rightXieBegin = i;
			}
		}
		for(int i = position[0] + 1; i <= position[0] + 4; i++){
			int j = position[1] + position[0] - i;
			if(i == 15 || j == -1 || gameStatus.pointStatus[i][j] == GameStatus.WHITE){
				rightXieEnd = i - 1;
				break;
			}else{
				rightXieEnd = i;
			}
		}
		//leftXie
		for(int i = position[0] - 1; i >= position[0] - 4; i--){
			int j = position[1] + i - position[0];
			if(i == -1 || j == -1 || gameStatus.pointStatus[i][j] == GameStatus.WHITE){
				leftXieBegin = i + 1;
				break;
			}else{
				leftXieBegin = i;
			}
		}
		for(int i = position[0] + 1; i <= position[0] + 4; i++){
			int j = position[1] + i - position[0];
			if(i == 15 || j == 15 || gameStatus.pointStatus[i][j] == GameStatus.WHITE){
				leftXieEnd = i - 1;
				break;
			}else{
				leftXieEnd = i;
			}
		}
		//�����ס�˽��ֻ���˫��
		if(gameStatus.pointStatus[position[0]][position[1]] == GameStatus.WHITE){
			maybeRolePointRemove(position[0], position[1]);
			rolePointRemove(position[0], position[1]);
		}
		
		for(byte i = (byte)hengBegin; i <= hengEnd; i++){
			byte j = position[1];
			//ֻ�к�Ҫ����i,j
			if(gameStatus.pointStatus[i][j] != GameStatus.WHITE){
				
				collectPoint(i, j);
				
			}
			
		}
		
		for(byte j = (byte)shuBegin; j <= shuEnd; j++){
			byte i = position[0];
			if(gameStatus.pointStatus[i][j] != GameStatus.WHITE && !(i == position[0] && j == position[1])){
				collectPoint(i, j);
			}
		}
		
		for(byte i = (byte)leftXieBegin; i <= leftXieEnd; i++){
			byte j = (byte) (position[1] - position[0] + i);
			if(gameStatus.pointStatus[i][j] != GameStatus.WHITE && !(i == position[0] && j == position[1])){
				collectPoint(i, j);
			}
		}
		for(byte i = (byte)rightXieBegin; i <= rightXieEnd; i++){
			byte j = (byte) (position[1] + position[0] - i);
			if(gameStatus.pointStatus[i][j] != GameStatus.WHITE && !(i == position[0] && j == position[1])){
				collectPoint(i, j);
			}
		}
		
		//�ж�˫�����Ƿ����
		for(int t = 0; t < gameStatus.maybeRolePoint.size(); t++){
			byte[] thisPoint = gameStatus.maybeRolePoint.get(t);
			 CheckRolePointFirstResult finalResult = checkRolePoint.checkRolePointFinal(thisPoint);
		     if(finalResult.finalFlag == CheckRolePoint.YES){
		    	 gameStatus.rolePoint.add(new byte[]{thisPoint[0] , thisPoint[1]});
		     }
		}
		
		for(int t = 0; t < gameStatus.rolePoint.size(); t++){
			byte[] thisPoint = gameStatus.rolePoint.get(t);
			gameStatus.pointBlackScore[thisPoint[0]][thisPoint[1]][0] = -2000000f;
			gameStatus.pointBlackScore[thisPoint[0]][thisPoint[1]][1] = WeighValue.DEFAULT_MANY_LEVEL;
		}
		//�����з�˫�����ֿ��ɰ��壬����position��������Χ�ķ�ֵ�Լ����ֵ���Χ�ķ�ֵ
		Vector<byte[]> blackBackup = new Vector<byte[]>();
		
		for(int t = 0; t < gameStatus.rolePoint.size(); t++){
			byte[] thisPoint = gameStatus.rolePoint.get(t);
			if(!isMaybePoint(thisPoint[0], thisPoint[1])){
				if(gameStatus.pointStatus[thisPoint[0]][thisPoint[1]] == GameStatus.BLACK){
					blackBackup.add(thisPoint);
				}
				gameStatus.pointStatus[thisPoint[0]][thisPoint[1]] = GameStatus.WHITE;
			}
			
		}
		updateScore(position, GameStatus.BLACK, true);
		for(int t = 0; t < gameStatus.rolePoint.size(); t++){
			byte[] thisPoint = gameStatus.rolePoint.get(t);
			updateScore(thisPoint, GameStatus.BLACK, true);
		}
		//�ָ���ɫ
		for(int t = 0; t < gameStatus.rolePoint.size(); t++){
			byte[] thisPoint = gameStatus.rolePoint.get(t);
			if(!isMaybePoint(thisPoint[0], thisPoint[1])){
				gameStatus.pointStatus[thisPoint[0]][thisPoint[1]] = GameStatus.EMPTY;
			}
			
		}
		
		for(int t = 0; t < blackBackup.size(); t++){
			byte[] thisPoint = blackBackup.get(t);
			gameStatus.pointStatus[thisPoint[0]][thisPoint[1]] = GameStatus.BLACK;
		}
		
		
		
	}
	public void collectPoint(byte i, byte j){
		CheckRolePointFirstResult checkResult =  checkRolePoint.checkRolePointFirst(new byte[]{i, j});
		
		
		if(checkResult.finalFlag == CheckRolePoint.YES){
			//˫�Ļ��ߵ��߽���
			rolePointAdd(i, j);
			maybeRolePointRemove(i, j);
		}else if(checkResult.finalFlag == CheckRolePoint.FIVE){
			rolePointRemove(i, j);
			maybeRolePointRemove(i, j);
		}else if(checkResult.finalFlag == CheckRolePoint.MAYBE){
			maybeRolePointAdd(i, j);
			
		}else if(checkResult.finalFlag == CheckRolePoint.NO){
			
			rolePointRemove(i, j);
			maybeRolePointRemove(i , j);
		}
	}
	
	private boolean needCheckMaybePoint(byte x, byte y){
		return gameStatus.pointBlackScore[x][y][1] == WeighValue.DEFAULT_THREE_LEVEL || gameStatus.pointBlackScore[x][y][1] == WeighValue.DEFAULT_DOUBLETHREE || gameStatus.pointBlackScore[x][y][1] == WeighValue.DEFAULT_THREEFOUR || gameStatus.pointBlackScore[x][y][1] == WeighValue.DEFAULT_MANY_LEVEL;
	}
	private boolean maybeRolePointContains(byte x, byte y){
		boolean exsits = false;
		for(int i = 0; i < gameStatus.maybeRolePoint.size(); i++){
			if(gameStatus.maybeRolePoint.get(i)[0] == x && gameStatus.maybeRolePoint.get(i)[1] == y){
				exsits = true;
				break;
			}
		}
		return exsits;
	}
	private void maybeRolePointRemove(byte x, byte y){
		for(int i = 0; i < gameStatus.maybeRolePoint.size(); i++){
			if(gameStatus.maybeRolePoint.get(i)[0] == x && gameStatus.maybeRolePoint.get(i)[1] == y){
				gameStatus.maybeRolePoint.remove(i);
				break;
			}
		}
	}
	private void maybeRolePointAdd(byte x, byte y){
		boolean exsits = false;
		for(int i = 0; i < gameStatus.maybeRolePoint.size(); i++){
			if(gameStatus.maybeRolePoint.get(i)[0] == x && gameStatus.maybeRolePoint.get(i)[1] == y){
				exsits = true;
				break;
			}
		}
		if(!exsits){
			byte[] addPoint = new byte[2];
			addPoint[0] = x;
			addPoint[1] = y;
			gameStatus.maybeRolePoint.add(addPoint);
		}
	}
	
	private void rolePointAdd(byte x, byte y){
		boolean exsits = false;
		for(int i = 0; i < gameStatus.rolePoint.size(); i++){
			if(gameStatus.rolePoint.get(i)[0] == x && gameStatus.rolePoint.get(i)[1] == y){
				exsits = true;
				break;
			}
		}
		if(!exsits){
			byte[] addPoint = new byte[2];
			addPoint[0] = x;
			addPoint[1] = y;
			gameStatus.rolePoint.add(addPoint);
		}
	}
	private void rolePointRemove(byte x, byte y){
		for(int i = 0; i < gameStatus.rolePoint.size(); i++){
			if(gameStatus.rolePoint.get(i)[0] == x && gameStatus.rolePoint.get(i)[1] == y){
				gameStatus.rolePoint.remove(i);
				break;
			}
		}
	}
	
	
	private boolean isRolePoint(byte x, byte y){
		boolean result = false;
		for(int i = 0; i < gameStatus.rolePoint.size(); i++){
			if(gameStatus.rolePoint.get(i)[0] == x && gameStatus.rolePoint.get(i)[1] == y){
				return true;
			}
		}
		return result;
	}
	
	private boolean isMaybePoint(byte x, byte y){
		boolean result = false;
		for(int i = 0; i < gameStatus.maybeRolePoint.size(); i++){
			if(gameStatus.maybeRolePoint.get(i)[0] == x && gameStatus.maybeRolePoint.get(i)[1] == y){
				return true;
			}
		}
		return result;
	}
	

    private void throwWrong(){
    	int i = 1 / 0;
    }

	public boolean isCallStop() {
		return callStop;
	}

	public void setCallStop(boolean callStop) {
		this.callStop = callStop;
	}
    public void setDeep(int deep){
    	this.deep = deep;
    }
    
    public float[][][] getPlayerScore(){
    	if(AIcolor == GameStatus.BLACK){
    		return gameStatus.pointWhiteScore;
    	}else{
    		return gameStatus.pointBlackScore;
    	}
    }
}
