package com.tricxta.metronome;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class MainUi extends JFrame {

	//-----------------------------------------------------------------------------------
	// CONSTANTS
	//-----------------------------------------------------------------------------------
	
	private static final String BTN_PLAY_TXT = "►";
	private static final String BTN_STOP_TXT = "■";
	private static Color BG_COLOUR = new Color(39, 39, 39);
	private static Color FG_COLOUR = new Color(240, 240, 240);
	
	//-----------------------------------------------------------------------------------
	// PUBLIC FUNCTIONS
	//-----------------------------------------------------------------------------------
	
	//-----------------------------------------------------------------------------------
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				
				new MainUi().setVisible(true);
			}
		});
	}
	
	//-----------------------------------------------------------------------------------
	public MainUi()
	{
		soundProc_ = new SoundProcessor();
		
		new Thread(soundProc_).start();
		
		initialComponents();
	}
	
	
	//-----------------------------------------------------------------------------------
	// PRIVATE MEMBERS
	//-----------------------------------------------------------------------------------
	
	private JSpinner 		primaryTickSpinner_;
	private JSpinner 		secondaryTickSpinner_;
	private JSpinner		tempoSpinner_;
	private JButton			togglePlayBtn_;
	private JButton 		pinBtn_ = new JButton("");
	private SoundProcessor 	soundProc_;
	private BufferedImage 	pinnedImg_;
	private BufferedImage	unpinnedImg_;
	
	//-----------------------------------------------------------------------------------
	// PRIVATE FUNCTIONS
	//-----------------------------------------------------------------------------------
	
	//-----------------------------------------------------------------------------------
	private void initialComponents()
	{
		JLabel 			primaryTickLbl = new JLabel("Primary:");
		JLabel 			secondaryTickLbl = new JLabel("Secondary:");
		JLabel 			tempoLbl = new JLabel("Tempo(BPM):");
		GridBagLayout 	layout = new GridBagLayout();
		BackgroundPanel rootPanel = null;
		
		
		try {
			rootPanel = new BackgroundPanel(ImageIO.read(MainUi.class.getClassLoader().getResourceAsStream("bg_img.png")));
			pinnedImg_ = ImageIO.read(MainUi.class.getClassLoader().getResourceAsStream("pinned.png"));
			unpinnedImg_ = ImageIO.read(MainUi.class.getClassLoader().getResourceAsStream("unpinned.png"));
		}
		catch ( Exception ex) {
			rootPanel = new BackgroundPanel(new BufferedImage(1, 1, BufferedImage.TYPE_INT_BGR));
			pinnedImg_ = new BufferedImage(1, 1, BufferedImage.TYPE_INT_BGR);
			unpinnedImg_ = new BufferedImage(1, 1, BufferedImage.TYPE_INT_BGR);
		}
		
		pinBtn_ = new JButton(new ImageIcon(pinnedImg_));
		pinBtn_.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ( MainUi.this.isAlwaysOnTop()) {
					//unpin
					pinBtn_.setIcon(new ImageIcon(unpinnedImg_));
					MainUi.this.setAlwaysOnTop(false);
				}
				else {
					//pin
					pinBtn_.setIcon(new ImageIcon(pinnedImg_));
					MainUi.this.setAlwaysOnTop(true);
				}
			}
		});
		
		togglePlayBtn_ = new JButton(BTN_PLAY_TXT);
		
		togglePlayBtn_.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleTicking();
			}
		});
		
		super.setAlwaysOnTop(true);
		
		primaryTickLbl.setForeground(FG_COLOUR);
		secondaryTickLbl.setForeground(FG_COLOUR);
		tempoLbl.setForeground(FG_COLOUR);
		
		primaryTickSpinner_ = new JSpinner(new SpinnerNumberModel(4, 1, 8, 1));
		secondaryTickSpinner_ = new JSpinner(new SpinnerNumberModel(4, 1, 16, 1));
		tempoSpinner_ = new JSpinner(new SpinnerNumberModel(130, 40, 300, 10));
		
		primaryTickSpinner_.setPreferredSize(new Dimension(40, 20));
		secondaryTickSpinner_.setPreferredSize(new Dimension(40, 20));
		tempoSpinner_.setPreferredSize(new Dimension(40, 20));
		
		super.getContentPane().add(rootPanel);
		rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		rootPanel.setLayout(layout);
		rootPanel.setBackground(BG_COLOUR);
		

		rootPanel.add(pinBtn_, new GridBagConstraints(0, 0, 4, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		
		rootPanel.add(primaryTickLbl, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		rootPanel.add(primaryTickSpinner_, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		
		rootPanel.add(secondaryTickLbl, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		rootPanel.add(secondaryTickSpinner_, new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		
		rootPanel.add(tempoLbl, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		rootPanel.add(tempoSpinner_, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		
		rootPanel.add(togglePlayBtn_, new GridBagConstraints(1, 4, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
		
		
		//set padding borders to center content
		rootPanel.add(new JLabel(), new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		rootPanel.add(new JLabel(), new GridBagConstraints(3,5,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		super.validate();
		
		super.setSize(300, 300);
		super.setTitle("MetroSimpleNome");
		
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	//-----------------------------------------------------------------------------------
	private void toggleTicking()
	{
		if ( soundProc_.IsTicking()) {
			//stop ticking
			soundProc_.StopTicking();
			togglePlayBtn_.setText(BTN_PLAY_TXT);
		}
		else {
			//start ticking
			soundProc_.StartTicking(
					(int)primaryTickSpinner_.getValue(), 
					(int)secondaryTickSpinner_.getValue(), 
					(int)tempoSpinner_.getValue());
			
			togglePlayBtn_.setText(BTN_STOP_TXT);
		}
	}
}
//-----------------------------------------------------------------------------------
// EOF
//-----------------------------------------------------------------------------------
