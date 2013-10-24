package com.feldschau.mathtriangles;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class TriangleView extends View {
	Random rand = new Random();
	
	private final double PI = 3.14159;
	private float HALF_ANGLE;
	private final float TAN_THETA = (float)Math.sqrt(3);
	
	private final float STROKE_WIDTH = 5.0f;
	private final float scaleFactor = 0.87f;
		
	private int MAX_NUMBER = 15;
	private final int MIN_NUMBER = 2;
	
	private int screenHeight;
	private int screenWidth;
	
	private Point[] originalPoints;
	
	private final float TRIANGLE_HEIGHT_PERCENTAGE = 0.5f;
	private int triangleHeight;
	private int barOffset = 0;
	
	private final float TEXT_LOCATION_PERCENTAGE = 0.15f;
	private final float TEXT_HEIGHT_PERCENTAGE = 0.12f;  // Percent of Triangle Height
	private int textOffset;

	private float fontSize;
	private final int textYAdjustment = 15;

	public boolean isShowingAnswer = false;
	private enum Operation {
		ADDITION ("+"), SUBTRACTION ("-"), MULTIPLICATION ("x"), DIVISION ("÷");
		private String symbol;
		Operation (String symbol) {
			this.symbol = symbol;
		}
		public String symbol() { return symbol; }
	}
	private Operation operation;
	int top, lhs, rhs;
	
	@SuppressWarnings("deprecation")
	public TriangleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		HALF_ANGLE = (float) PI / 6.0f;
				
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

//		Point size = new Point();
//		display.getSize(size);
//		screenWidth = size.x;
//		screenHeight = size.y;
		
		screenWidth = display.getWidth();  // deprecated
		screenHeight = display.getHeight() - barOffset;  // deprecated
		
		triangleHeight = (int) (screenHeight * TRIANGLE_HEIGHT_PERCENTAGE);
		textOffset = (int) (screenHeight * TEXT_LOCATION_PERCENTAGE);
		
		originalPoints = new Point[3];
		
		for (int i = 0; i < originalPoints.length; i++) {
			originalPoints[i] = new Point();			
		}
		
		originalPoints[0].x = screenWidth / 2;
		originalPoints[0].y = screenHeight / 2 - triangleHeight * 3 / 4;		// 1/4 triangle height is below screen height midpoint for balance
		originalPoints[1].x = (int) (originalPoints[0].x - (triangleHeight / TAN_THETA)); 
		originalPoints[1].y = originalPoints[0].y + triangleHeight;	
		originalPoints[2].x = (int) (originalPoints[0].x + (triangleHeight / TAN_THETA));
		originalPoints[2].y = originalPoints[1].y;		
		
		setBackgroundColor(0xFFFFFFFF);
		
		fontSize = triangleHeight * TEXT_HEIGHT_PERCENTAGE;
		
		setNewOperation();
	}
		
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
				
		if (!isShowingAnswer) {
			setNewValues();
		}
		
		drawRoundedTriangle(canvas, convertFactorToOffset(scaleFactor));		 
		drawText(canvas);		
	}
	
	private void drawText(Canvas canvas) {
		Paint textPaint = new Paint();
		textPaint.setTextSize(fontSize);
		textPaint.setTypeface(Typeface.create("Arial", Typeface.BOLD));
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setColor(Color.BLACK);
		
		Paint textPaintRed = new Paint();
		textPaintRed.setTextSize(fontSize);
		textPaintRed.setTypeface(Typeface.create("Arial", Typeface.BOLD));
		textPaintRed.setTextAlign(Paint.Align.CENTER);
		textPaintRed.setColor(Color.RED);

		Point[] textTriangle = getScaledTriangle(textOffset);
		
		for (int i = 0; i < textTriangle.length; i++) {
			textTriangle[i].y += textYAdjustment;
		}
		
		float mpy = (float)(Math.tan(PI / 6)) * (((float)triangleHeight) / ((float)TAN_THETA));
		
		canvas.drawText(operation.symbol(),
				originalPoints[0].x, 
				originalPoints[0].y + (triangleHeight - mpy) - (fontSize / 2) + textYAdjustment, 
				textPaint);
		// Always draw the left hand side
		canvas.drawText(Integer.toString(lhs), textTriangle[1].x, textTriangle[1].y, textPaint);  
				
		if (Operation.ADDITION == operation || Operation.MULTIPLICATION == operation) {
			canvas.drawText(Integer.toString(rhs), textTriangle[2].x, textTriangle[2].y, textPaint);
			
			if (isShowingAnswer) {
				canvas.drawText(Integer.toString(top), textTriangle[0].x, textTriangle[0].y, textPaintRed);
			}
		} else {
			canvas.drawText(Integer.toString(top), textTriangle[0].x, textTriangle[0].y, textPaint);
			
			if (isShowingAnswer) {
				canvas.drawText(Integer.toString(rhs), textTriangle[2].x, textTriangle[2].y, textPaintRed);
			}
		}
	}
	
	public void setNewValues() {
		setNewOperation();
		
		MAX_NUMBER = Integer.parseInt(
				PreferenceManager.getDefaultSharedPreferences(this.getContext())
				.getString(SettingsActivity.KEY_PREF_MAX_NUMBER, "15"));
		if (Operation.ADDITION == operation || Operation.SUBTRACTION == operation) {
			top = rand.nextInt(MAX_NUMBER - MIN_NUMBER + 1) + MIN_NUMBER; // provides a number between MAX_NUMBER and MIN_NUMBER, inclusive
			lhs = rand.nextInt(top - 1) + 1;  // number between 1 and sum-1, inclusive
			
			rhs = top - lhs;
		} else {
			MAX_NUMBER = Integer.parseInt(
					PreferenceManager.getDefaultSharedPreferences(this.getContext())
					.getString(SettingsActivity.KEY_PREF_MAX_DIVISOR, "5"));
			lhs = rand.nextInt(MAX_NUMBER) + 1;
			rhs = rand.nextInt(MAX_NUMBER) + 1;
			
			top = lhs * rhs;			
		}
	}

	public void setNewOperation() {		
		ArrayList<TriangleView.Operation> tempOpList = new ArrayList<TriangleView.Operation>(); 
		
		boolean addition = PreferenceManager.getDefaultSharedPreferences(this.getContext())
				.getBoolean("checkbox_addition", true);
		boolean subtraction = PreferenceManager.getDefaultSharedPreferences(this.getContext())
				.getBoolean("checkbox_subtraction", true);
		boolean multiplication = PreferenceManager.getDefaultSharedPreferences(this.getContext())
				.getBoolean("checkbox_multiplication", true);
		boolean division = PreferenceManager.getDefaultSharedPreferences(this.getContext())
				.getBoolean("checkbox_division", true);
		
		// check that something is selected, if not, enable addition
		if (! (addition || subtraction || multiplication || division) ) {
				Editor e = PreferenceManager.getDefaultSharedPreferences(this.getContext()).edit();
				e.putBoolean("checkbox_addition", true);
				e.commit();
				
				addition = true;
		}
		
		if(addition) { tempOpList.add(Operation.ADDITION); }
		if(subtraction) { tempOpList.add(Operation.SUBTRACTION); }
		if(multiplication) { tempOpList.add(Operation.MULTIPLICATION); }
		if(division) { tempOpList.add(Operation.DIVISION); }
		
		operation = tempOpList.get(rand.nextInt(tempOpList.size()));
	}
	
	private void drawRoundedTriangle(Canvas canvas, int offset) {
		Point[] tempPoints = getScaledTriangle(offset);
		
		Paint trianglePaint = new Paint();
		trianglePaint.setColor(Color.BLACK);
		trianglePaint.setStrokeWidth(STROKE_WIDTH);
		// Top, Left and Right rounded corners
		drawCircularArc(canvas, tempPoints[0].x, tempPoints[0].y + 1, (int) (Math.sin(HALF_ANGLE) * offset), 209, 331);
		drawCircularArc(canvas, tempPoints[1].x, tempPoints[1].y + 1, (int) (Math.sin(HALF_ANGLE) * offset), 89, 211);
		drawCircularArc(canvas, tempPoints[2].x, tempPoints[2].y + 1, (int) (Math.sin(HALF_ANGLE) * offset), -31, 91);
		
		tempPoints = pushPointsToEdge(tempPoints, offset);
		// See pushPointsToEdge method for tempPoints layout
		canvas.drawLine(tempPoints[0].x, tempPoints[0].y, tempPoints[1].x, tempPoints[1].y, trianglePaint);
		canvas.drawLine(tempPoints[2].x, tempPoints[2].y, tempPoints[3].x, tempPoints[3].y, trianglePaint);
		canvas.drawLine(tempPoints[4].x, tempPoints[4].y, tempPoints[5].x, tempPoints[5].y, trianglePaint);
	}	
	
	private void drawCircularArc(Canvas canvas, int x, int y, float radius, int startDegAngle, int endDegAngle) {
		RectF oval = new RectF(
				(float)x - radius, 
				(float)y - radius, 
				(float)x + radius, 
				(float)y + radius);
		
		Paint arcPaint = new Paint();
		arcPaint.setStyle(Paint.Style.STROKE);
		arcPaint.setStrokeWidth(STROKE_WIDTH);
		
		canvas.drawArc(oval, startDegAngle, endDegAngle - startDegAngle, false, arcPaint);
	}
	
	private int convertFactorToOffset(float scaleFactor) {
		return (int) ((1-scaleFactor) * triangleHeight);
	}
	
	private Point[] getScaledTriangle(int offset) {
		Point [] trianglePoints = new Point[3];
		for (int i = 0; i < trianglePoints.length; i++) {
			trianglePoints[i] = new Point();
		}
		
		trianglePoints[0].x = originalPoints[0].x;
		trianglePoints[0].y = originalPoints[0].y + offset;
		trianglePoints[1].x = (int) (originalPoints[1].x + (Math.cos(PI / 6) * offset));
		trianglePoints[1].y = (int) (originalPoints[1].y - (Math.sin(PI / 6) * offset));
		trianglePoints[2].x = (int) (originalPoints[2].x - (Math.cos(PI / 6) * offset));
		trianglePoints[2].y = trianglePoints[1].y;
		
		return trianglePoints;
	}
	
	private Point[] pushPointsToEdge(Point[] points, int offset) {
		Point[] newPoints = new Point[6];
		for (int i = 0; i < newPoints.length; i++) {
			newPoints[i] = new Point();
		}
		/*
		 * 		 04				 0
		 * 	    1  5
		 * 		 23 		  1	    2
		 */
		
		// Left Edge
		newPoints[0].x = points[0].x - (int) (Math.cos(HALF_ANGLE) * Math.sin(HALF_ANGLE) * offset);
		newPoints[0].y = points[0].y - (int) (Math.sin(HALF_ANGLE) * Math.sin(HALF_ANGLE) * offset);
		newPoints[1].x = points[1].x - (int) (Math.cos(HALF_ANGLE) * Math.sin(HALF_ANGLE) * offset);
		newPoints[1].y = points[1].y - (int) (Math.sin(HALF_ANGLE) * Math.sin(HALF_ANGLE) * offset);
		
		// Bottom Edge
		newPoints[2].x = points[1].x;
		newPoints[2].y = points[1].y + (int) (Math.sin(HALF_ANGLE) * offset);
		newPoints[3].x = points[2].x;
		newPoints[3].y = points[2].y + (int) (Math.sin(HALF_ANGLE) * offset);
		
		// Right Edge
		newPoints[4].x = points[0].x + (int) (Math.cos(HALF_ANGLE) * Math.sin(HALF_ANGLE) * offset);
		newPoints[4].y = points[0].y - (int) (Math.sin(HALF_ANGLE) * Math.sin(HALF_ANGLE) * offset);
		newPoints[5].x = points[2].x + (int) (Math.cos(HALF_ANGLE) * Math.sin(HALF_ANGLE) * offset);
		newPoints[5].y = points[2].y - (int) (Math.sin(HALF_ANGLE) * Math.sin(HALF_ANGLE) * offset);
		
		return newPoints;
	}
}
