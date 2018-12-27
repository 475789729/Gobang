package com.liuyao.gobang.widget;

import java.util.ArrayList;
import java.util.List;

import com.liuyao.gobang.ui.R;

import net.liuyao.core.GameStatus;
import net.liuyao.core.Gobang;
import net.liuyao.core.Gobang.IDrawChess;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import android.widget.Toast;
//棋盘
public class Chessboard extends View{

	private Gobang game;
    
	//画笔对象
	private final Paint paint = new Paint();
	
	private Bitmap black;
	private Bitmap white;
	
	//点大小
    private static int pointSize = 20;
	
	private float lineSpace;
	//棋盘边界距离view边框的距离
	private int boder = 10;
	private boolean xuhao = false;
	
	

	
    public Chessboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        
        
        //设置画线时用的颜色
        paint.setColor(getResources().getColor(android.R.color.black));
       
   }
   
       @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	// TODO Auto-generated method stub
    	super.onSizeChanged(w, h, oldw, oldh);
    	lineSpace = (w - 2 * boder) /(float)14;
    	pointSize = (int) (lineSpace * 0.95f);
    	initBitmap();              
         createLines();
         invalidate();
    	
    }
    private void initBitmap(){
    	black = BitmapFactory.decodeResource(getResources(), R.drawable.black);
        white = BitmapFactory.decodeResource(getResources(), R.drawable.white);
        black = Bitmap.createScaledBitmap(black, pointSize, pointSize, true);
        white = Bitmap.createScaledBitmap(white, pointSize, pointSize, true);
    }
    
    //产生棋盘上所有的线
    private void createLines(){
    	for (int i = 0; i < 15; i++) {//竖线
    		lines.add(new Line(boder+i*lineSpace, boder, boder+i*lineSpace, getWidth() - boder));
		}
    	for (int i = 0; i < 15; i++) {//横线
    		lines.add(new Line(boder, boder+i*lineSpace, getWidth() - boder, boder+i*lineSpace));
		}
    }
    
    //画棋盘
    private List<Line> lines = new ArrayList<Line>();
    private void drawChssboardLines(Canvas canvas){
    	for (int i = 0; i < lines.size(); i++) {
    		if(i == 0 || i == 14 || i == 15 || i == 29){
    			paint.setStrokeWidth(2f);
    		}	 	
    		canvas.drawLine(lines.get(i).xStart, lines.get(i).yStart, lines.get(i).xStop, lines.get(i).yStop, paint);
    		if(i == 0 || i == 14 || i == 15 || i == 29){
    			paint.setStrokeWidth(0f);
    		}	
    	}
    	
    
    	canvas.drawCircle(boder + 3 * lineSpace, boder + 3 * lineSpace, lineSpace/10, paint);
    	canvas.drawCircle(boder + 11 * lineSpace, boder + 3 * lineSpace, lineSpace/10, paint);
    	canvas.drawCircle(boder + 3 * lineSpace, boder + 11 * lineSpace, lineSpace/10, paint);
    	canvas.drawCircle(boder + 11 * lineSpace, boder + 11 * lineSpace, lineSpace/10, paint);
    	canvas.drawCircle(boder + 7 * lineSpace, boder + 7 * lineSpace, lineSpace/10, paint);
    }
    
    //线类
    class Line{
    	float xStart,yStart,xStop,yStop;
		public Line(float xStart, float yStart, float xStop, float yStop) {
			this.xStart = xStart;
			this.yStart = yStart;
			this.xStop = xStop;
			this.yStop = yStop;
		}
    }
    

	//根据触摸点坐标找到对应点
	private Point newPoint(Float x, Float y){
		Point p = new Point(-1, -1);
		for (int i = 0; i < 15; i++) {
			if (Math.abs(i * lineSpace + boder - x) <= lineSpace / 2
					) {
				p.setX(i);
				break;
			}
		}
		for (int i = 0; i < 15; i++) {
			if (Math.abs(i * lineSpace + boder - y) <= lineSpace / 2
					) {
				p.setY(i);
				break;
			}
		}
		return p;
	}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	
    	if(game == null){
    		return super.onTouchEvent(event);
    	}else if(game.getTurn() == game.playerColor && game.gameStatus.winner == GameStatus.EMPTY){
    		if(event.getAction()==MotionEvent.ACTION_DOWN){
    			return true;
    		}
    		if(event.getAction()==MotionEvent.ACTION_UP){
    		
    			Point p = newPoint(event.getX(), event.getY());
    			
    			if(p.x >= 0 && p.y >= 0 && game.gameStatus.pointStatus[p.x][p.y] == GameStatus.EMPTY){
    				for(int i = 0; i < game.gameStatus.rolePoint.size(); i++){
    					byte[] rolePoint = game.gameStatus.rolePoint.get(i);
    					if(p.x == rolePoint[0] && p.y == rolePoint[1] && game.aiColor == GameStatus.WHITE){
    						Toast.makeText(getContext(), "禁手!", Toast.LENGTH_SHORT).show();
    						return true;
    					}
    				}
    				game.playerStep(new byte[]{(byte) (p.x),(byte) (p.y)});
    				game.AIStep();
    				return true;
    			}
        	}
    	}
    	
    	return super.onTouchEvent(event);
    }
	
   
	private void drawGobangPoint(Canvas canvas){
		for(int i = 0; i < game.gameStatus.historyPoint.size(); i++){
			byte[] position = game.gameStatus.historyPoint.get(i);
			if(game.gameStatus.pointStatus[position[0]][position[1]] == GameStatus.BLACK){
				canvas.drawBitmap(black, boder + position[0] * lineSpace - black.getHeight()/2f, boder + position[1] * lineSpace - black.getHeight()/2f, paint);
			}else{
				canvas.drawBitmap(white, boder + position[0] * lineSpace - black.getHeight()/2f, boder + position[1] * lineSpace - black.getHeight()/2f, paint);
			}
			if(xuhao){
				paint.setTextSize(pointSize * 0.4f);
				FontMetricsInt fontMetrics = paint.getFontMetricsInt();
				float textHeight = fontMetrics.bottom - fontMetrics.top;
				if(i % 2 == 0){
					paint.setColor(getResources().getColor(R.color.white));
				}else{
					paint.setColor(getResources().getColor(R.color.black));
				}
				
				paint.setTextAlign(Paint.Align.CENTER);
			    float baseline = boder + position[1] * lineSpace + textHeight/4;
				canvas.drawText((i+1)+"", boder + position[0] * lineSpace, baseline, paint);
				paint.setColor(getResources().getColor(android.R.color.black));
			}
			if(i == game.gameStatus.historyPoint.size() - 1){
				paint.setColor(getResources().getColor(R.color.red));
				canvas.drawCircle(boder + position[0] * lineSpace, boder + position[1] * lineSpace, lineSpace/10, paint);
				paint.setColor(getResources().getColor(android.R.color.black));
			}
		}
		
	}
	
    
    //doRun方法操作的是看不见的内存数据，此方法内容数据以图画的方式表现出来，所以画之前数据一定要先准备好
    @Override
    protected void onDraw(Canvas canvas) {
    	drawChssboardLines(canvas);
    	if(game != null){
    		drawGobangPoint(canvas);
    	}
    }

	public Gobang getGame() {
		return game;
	}

	public void setGame(Gobang game) {
		this.game = game;
		if(game != null){
			this.game.setDrawChess(new IDrawChess() {
				
				@Override
				public void drawChess() {
					// TODO Auto-generated method stub
					invalidate();
				}
			});
		}
		
		
	}

	public boolean isXuhao() {
		return xuhao;
	}

	public void setXuhao(boolean xuhao) {
		this.xuhao = xuhao;
	}
    
}
