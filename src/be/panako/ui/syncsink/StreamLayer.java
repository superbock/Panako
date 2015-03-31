package be.panako.ui.syncsink;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import be.tarsos.dsp.ui.Axis;
import be.tarsos.dsp.ui.CoordinateSystem;
import be.tarsos.dsp.ui.layers.Layer;
import be.tarsos.dsp.ui.layers.LayerUtilities;

public class StreamLayer implements Layer, MouseListener{
	
	private final int index;
	private final Color color;
	private final String description;
	private final boolean isReference;
	private final CoordinateSystem cs;
	private final List<Float> startTimes;//in ms
	private final List<Float> stopTimes;//in ms
	private final float streamDuration;//duration in ms
	private float guessedStartTimeOfStream;
	private Graphics2D graphics;
	private final List<File> dataFiles;
	
	public StreamLayer(CoordinateSystem cs,int index, Color color,String description, boolean isReference,float streamDuration){
		this.index = index;
		this.color = color;
		this.description = description;
		this.isReference = isReference;
		this.cs = cs;
		this.startTimes = new ArrayList<Float>();
		this.stopTimes = new ArrayList<Float>();
		this.streamDuration = streamDuration;
		this.dataFiles = new ArrayList<File>();
	}
	
	public void addInterval(float startTimeInReference, float stopTimeInReference,float startTimeInResource, float stopTimeInResource){
		startTimes.add(startTimeInReference);
		stopTimes.add(stopTimeInReference);
		guessedStartTimeOfStream = (startTimeInReference - startTimeInResource);
	}
	
	

	@Override
	public void draw(Graphics2D graphics) {
		this.graphics = graphics;
		float spacer = LayerUtilities.pixelsToUnits(graphics, 20, false);
		float heightOfABlock = LayerUtilities.pixelsToUnits(graphics, 30, false);
		
		int verticalOffsetOffset = -1 * (Math.round((index + 1) * spacer + index * heightOfABlock));
		
		//draw dotted lines
		if(isReference){
			int startTime = Math.round(startTimes.get(0));
			int stopTime = Math.round(stopTimes.get(0));
			
			int grayScale = 80;
			Color lightGray = new Color(grayScale,grayScale,grayScale,grayScale);
			graphics.setColor(lightGray);
			int maxY = Math.round(cs.getMax(Axis.Y));
			int minY = Math.round(cs.getMin(Axis.Y));
			graphics.drawLine(startTime, minY, startTime, maxY);
			graphics.drawLine(stopTime, minY, stopTime, maxY);
		}else{
			int startTime = Math.round(guessedStartTimeOfStream);
			int stopTime = Math.round(guessedStartTimeOfStream+streamDuration);
			
			Color backgroundColor = Color.LIGHT_GRAY;
			graphics.setColor(backgroundColor);
			//graphics.fillRect(startTime, verticalOffsetOffset, stopTime-startTime, Math.round(heightOfABlock));
			graphics.drawRect(startTime, verticalOffsetOffset, stopTime-startTime, Math.round(heightOfABlock));
		}
		
		
		
		for(int i = 0 ; i < stopTimes.size() ; i++){
			
			int startTime = Math.round(startTimes.get(i));
			int stopTime = Math.round(stopTimes.get(i));
			
			Color backgroundColor = getBackgroundColor();
			graphics.setColor(backgroundColor);
			graphics.fillRect(startTime, verticalOffsetOffset, stopTime-startTime, Math.round(heightOfABlock));
			
			//a block 
			graphics.setColor(color);
			graphics.drawRect(startTime, verticalOffsetOffset, stopTime-startTime, Math.round(heightOfABlock));
			
			float verticalTextPosition = verticalOffsetOffset + heightOfABlock/2.0f;
			LayerUtilities.drawString(graphics, description, (stopTime+startTime)/2.0f, verticalTextPosition , true, true, null);
		}
		
		
	
		
	}
	
	private Color getBackgroundColor(){
		float[] components = new float[3];
		color.getColorComponents(components);
		Color backgroundColor = new Color(ColorSpace.getInstance(ColorSpace.CS_sRGB),components , 0.13f);
		return backgroundColor;
	}

	@Override
	public String getName() {
		return "StreamLayer";
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(graphics!=null){
			float spacer = LayerUtilities.pixelsToUnits(graphics, 20, false);
			float heightOfABlock = LayerUtilities.pixelsToUnits(graphics, 30, false);		
			int verticalOffsetOffset = -1 * (Math.round((index + 1) * spacer + index * heightOfABlock));
			int startHeight = verticalOffsetOffset ;
			int stopHeight = verticalOffsetOffset + Math.round(heightOfABlock); 
	
			Point2D pointInUnits = LayerUtilities.pixelsToUnits(graphics, e.getX(), e.getY());
			int startTime = Math.round(guessedStartTimeOfStream);
			int stopTime = Math.round(guessedStartTimeOfStream+streamDuration);
			if(pointInUnits.getX() >= startTime && pointInUnits.getX() <= stopTime && pointInUnits.getY() >= startHeight &&  pointInUnits.getY() <= stopHeight){
				System.out.println("Click in layer " + index);
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Choose corresponding data file.");
			    int returnVal = chooser.showOpenDialog(null);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	File file = chooser.getSelectedFile();
			    	this.dataFiles.add(file);	
			    }
			}
		}
	}
	
	public List<File> getDataFiles(){
		return this.dataFiles;
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}



}