package gui_projekt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils { 
	public static String path = new String();
	public static File database = null;
	public static List<Image> images = new LinkedList<Image>();
	public static int imagesCount = images.size();
	
	public static JFrame mainFrame = new JFrame();
	public static JPanel imagesPanel = new JPanel();
	public static JPanel menuPanel = new JPanel();
	
	public static JFileChooser fileChooser = new JFileChooser();
	public static Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();

	
	public static void clearImagesPanel() {
		imagesPanel.removeAll();
		reloadGUI();
	}
	
	public static void reloadGUI() {
		mainFrame.revalidate();
		mainFrame.repaint();
	}
	
	public static void showGUI() {		
		JButton getDBbutton = new JButton("Wczytaj bazê");
		
		getDBbutton.setBackground(new Color(135, 206, 235));
		getDBbutton.setFont(new Font("Calibri", Font.PLAIN, 35));
				
		mainFrame.setLayout(new GridBagLayout());
		mainFrame.add(getDBbutton, new GridBagConstraints());
		
		FileNameExtensionFilter restrict = new FileNameExtensionFilter("Tylko pliki .txt!", "txt");
		fileChooser.addChoosableFileFilter(restrict);
		fileChooser.setAcceptAllFileFilterUsed(false); 
		
		getDBbutton.addActionListener(event -> {
			int result = fileChooser.showOpenDialog(mainFrame);
			if(result == JFileChooser.APPROVE_OPTION) {
				path = fileChooser.getSelectedFile().getAbsolutePath();
				database = new File(path);
				readDB();
				
				mainFrame.remove(getDBbutton);
				
				mainFrame.setLayout(new BorderLayout(0, 0));
				
				showMenu();
				showAll();
				reloadGUI();
			}
		});
		
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		WindowListener exitListener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				String[] optionsNoDB = {"Zamknij bez zapisywania (nie wczytano bazy)", "Nie zamykaj"};
				String[] options = {"Tak", "Zamknij bez zapisywania", "Nie zamykaj"};
				int confirm;
				if(database != null) {
					confirm = JOptionPane.showOptionDialog(
							null, "Zapisaæ bazê i zamkn¹æ program?",
							"Wybierz opcjê", JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, options, null);
					if(confirm == 0) {
						saveDB(path);
						System.exit(0);
					} else if(confirm == 1) {
						System.exit(0);
					}
				} else {
					confirm = JOptionPane.showOptionDialog(
							null, "Zapisaæ bazê i zamkn¹æ program?",
							"Wybierz opcjê", JOptionPane.DEFAULT_OPTION,
							JOptionPane.INFORMATION_MESSAGE, null, optionsNoDB, null);
					if(confirm == 0) {
						System.exit(0);
					}
				}
			}
		};
		
		mainFrame.addWindowListener(exitListener);
		
		mainFrame.setVisible(true);
		mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);	
		
		mainFrame.getContentPane().setBackground(new Color(25, 25, 25));
		mainFrame.setTitle("Przegl¹darka obrazów - projekt na GUI (by Jakub Sobolewski)");
	}
	
	public static void showMenu() {
		JButton addImageButton = new JButton("Dodaj obraz");
		addImageButton.addActionListener(event -> {			
			Image img = new Image();
			
			JTextField dirField = new JTextField(100);
			dirField.setEditable(false);
			JTextField authorField = new JTextField(50);
			JTextField placeField = new JTextField(15);
			JTextField dateField = new JTextField(15);
			JTextField tagsField = new JTextField(1000);
			
			JButton chooseImgFileButton = new JButton("Wczytaj plik");
			JFileChooser fileChooserImg = new JFileChooser();
			
			chooseImgFileButton.addActionListener(nextEvent -> {
				int result = fileChooserImg.showOpenDialog(Utils.mainFrame);
				if(result == JFileChooser.APPROVE_OPTION) {
					String imgPath = fileChooserImg.getSelectedFile().getPath();
					dirField.setText(imgPath);
				}
			});
			
			JPanel addingPanel = new JPanel();
			
			addingPanel.setLayout(new BoxLayout(addingPanel, BoxLayout.Y_AXIS));
			addingPanel.setPreferredSize(new Dimension(1000, 250));
			
			addingPanel.add(new JLabel("Lokalizacja obrazu: "));
			addingPanel.add(dirField);
			addingPanel.add(chooseImgFileButton);
			
			addingPanel.add(new JLabel("Autor: "));
			addingPanel.add(authorField);
			
			addingPanel.add(new JLabel("Miejsce: "));
			addingPanel.add(placeField);
			addingPanel.add(new JLabel("Data (w formacie: dd.mm.rrrr): "));
			addingPanel.add(dateField);

			addingPanel.add(new JLabel("Tagi (oddzielone przecinkiem i spacj¹ - np. \"tag1, tag2, tag3\"): "));
			addingPanel.add(tagsField);

			int result = JOptionPane.showConfirmDialog(null, addingPanel,
					"WprowadŸ dane obrazu, który chcesz dodaæ", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				img.id = imagesCount;
				img.dir = dirField.getText();
				img.author = authorField.getText();
				img.place = placeField.getText();
				int[] tempDate = splitDate(dateField.getText());
				img.day = tempDate[0];
				img.month = tempDate[1];
				img.year = tempDate[2];
				img.tags = tagsField.getText().split(", ");
				if(searchByDir(img.dir) == -1) {
					img.addToDB();
					img.show();
					reloadGUI();
				}
			}
		});
		JButton editImageButton = new JButton("Edytuj obraz");
		editImageButton.addActionListener(event -> {
			JTextField dirField = new JTextField(100);
			JTextField authorField = new JTextField(50);
			JTextField placeField = new JTextField(15);
			JTextField dateField = new JTextField(15);
			JTextField tagsField = new JTextField(1000);
			
			String[] imagesStringTab = new String[imagesCount];
			for(int i = 0; i < imagesCount; i++) {
				imagesStringTab[images.get(i).id] = images.get(i).getDir();
			}
			
			JComboBox<String> chooseImgBox = new JComboBox<>(imagesStringTab);
			
			JPanel chooseImgToEditPanel = new JPanel();
			chooseImgToEditPanel.add(chooseImgBox);
					
			int result = JOptionPane.showConfirmDialog(null, chooseImgToEditPanel,
					"Wybierz, który obraz chcesz edytowaæ", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				JPanel editImgFormPanel = new JPanel();
				editImgFormPanel.setLayout(new BoxLayout(editImgFormPanel, BoxLayout.Y_AXIS));
				editImgFormPanel.setPreferredSize(new Dimension(1000, 250));

				String dir = (String)chooseImgBox.getSelectedItem();
				dirField.setText(dir);
				
				Image tempImg = images.get(searchByDir(dir));
				
				int tempImgId = tempImg.getId();
				
				editImgFormPanel.add(new JLabel("Lokalizacja: "));
				editImgFormPanel.add(dirField);
				authorField.setText(tempImg.getAuthor());
				editImgFormPanel.add(new JLabel("Autor: "));
				editImgFormPanel.add(authorField);
				placeField.setText(tempImg.getPlace());
				editImgFormPanel.add(new JLabel("Miejsce: "));
				editImgFormPanel.add(placeField);
				dateField.setText(tempImg.getDay() + "." + tempImg.getMonth() + "." + tempImg.getYear());
				editImgFormPanel.add(new JLabel("Data: "));
				editImgFormPanel.add(dateField);
				tagsField.setText(tempImg.getTags());
				editImgFormPanel.add(new JLabel("Tagi: "));
				editImgFormPanel.add(tagsField);
								
				int resultForm = JOptionPane.showConfirmDialog(null, editImgFormPanel,
						"Zmieñ dane", JOptionPane.OK_CANCEL_OPTION);
				
				if(resultForm == JOptionPane.OK_OPTION) {
					if(!(dirField.getText().equals(tempImg.getDir()))) {
						images.get(tempImgId).setDir(dirField.getText());
					}
					if(!(authorField.getText().equals(tempImg.getAuthor()))) {
						images.get(tempImgId).setAuthor(authorField.getText());
					}
					if(!(placeField.getText().equals(tempImg.getPlace()))) {
						images.get(tempImgId).setPlace(placeField.getText());
					}
					if(!(dateField.getText().equals(tempImg.getDate()))) {
						images.get(tempImgId).setDate(dateField.getText());
					}
					if(!(tagsField.getText().equals(tempImg.getTags()))) {
						images.get(tempImgId).setTags(tagsField.getText());
					}
				}
			}
			
			reloadGUI();
		});
		JButton removeImageButton = new JButton("Usuñ obraz");
		removeImageButton.addActionListener(event -> {
			String[] imagesStringTab = new String[imagesCount];
			for(Image i : images) {
				imagesStringTab[i.id] = i.getDir();
			}
			
			JComboBox<String> chooseImgBox = new JComboBox<>(imagesStringTab);
			
			JPanel chooseImgToRemovePanel = new JPanel();
			chooseImgToRemovePanel.add(chooseImgBox);
					
			int result = JOptionPane.showConfirmDialog(null, chooseImgToRemovePanel,
					"Wybierz, który obraz chcesz usun¹æ", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				String dir = (String)chooseImgBox.getSelectedItem();
				
				imagesPanel.remove(images.get(searchByDir(dir)).getLabel());
				
				images.get(searchByDir(dir)).removeFromDB();
				reloadGUI();
			}
		});
		JButton showSingleImgButton = new JButton("Wyswietl wybrany obraz");
		showSingleImgButton.addActionListener(event -> {
			String[] imagesStringTab = new String[imagesCount];
			for(Image i : images) {
				imagesStringTab[i.id] = i.getDir();
			}
			
			JComboBox<String> chooseImgBox = new JComboBox<>(imagesStringTab);
			
			JPanel chooseImgToShowPanel = new JPanel();
			chooseImgToShowPanel.add(chooseImgBox);
			
			int result = JOptionPane.showConfirmDialog(null, chooseImgToShowPanel,
					"Wybierz, który obraz chcesz wyœwietliæ", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				clearImagesPanel();
				String dir = (String)chooseImgBox.getSelectedItem();
				images.get(searchByDir(dir)).show();
				reloadGUI();
			}
		});
		JButton showAllButton = new JButton("Wyœwietl wszystkie obrazy");
		showAllButton.addActionListener(event -> {
			showAll();
		});
		JButton searchByTagButton = new JButton("Wyœwietl obrazy na podstawie tagu");
		searchByTagButton.addActionListener(event -> {
			JPanel searchByTagPanel = new JPanel();
			JTextField tagField = new JTextField(10);
			searchByTagPanel.add(tagField);
					
			int result = JOptionPane.showConfirmDialog(null, searchByTagPanel,
					"Wpisz tag", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				showByTag(tagField.getText());
				reloadGUI();
			}
		});
		JButton searchByParameterButton = new JButton("Wyszukaj na podstawie...");
		searchByParameterButton.addActionListener(event -> {
			String[] parametersTab = new String[] {"Œcie¿ka", "Autor", "Miejsce", "Data", "Tagi"};
			JComboBox<String> chooseParameterBox = new JComboBox<>(parametersTab);
			
			JPanel chooseParameterPanel = new JPanel();
			chooseParameterPanel.add(chooseParameterBox);
			chooseParameterPanel.setPreferredSize(new Dimension(200, 50));
			
			int result = JOptionPane.showConfirmDialog(null, chooseParameterPanel,
					"Wybierz parametr do wyszukania", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				String chosenParameter = (String)chooseParameterBox.getSelectedItem();
				Image foundImage = new Image();
				switch(chosenParameter) {
				case "Œcie¿ka":
					JPanel searchByDirPanel = new JPanel();
					JTextField dirField = new JTextField(100);
					searchByDirPanel.add(dirField);
					int resultNext = JOptionPane.showConfirmDialog(null, searchByDirPanel,
							"Wpisz œcie¿kê do wyszukania", JOptionPane.OK_CANCEL_OPTION);
					if(resultNext == JOptionPane.OK_OPTION) {
						clearImagesPanel();
						if(searchByDir(dirField.getText()) != -1) {
							foundImage = images.get(searchByDir(dirField.getText()));
							System.out.println(foundImage.toString());
							foundImage.show();
						}
					}
					break;
				case "Autor":
					JPanel searchByAuthorPanel = new JPanel();
					JTextField authorField = new JTextField(50);
					searchByAuthorPanel.add(authorField);
					int resultNext2 = JOptionPane.showConfirmDialog(null, searchByAuthorPanel,
							"Wpisz autora do wyszukania", JOptionPane.OK_CANCEL_OPTION);
					if(resultNext2 == JOptionPane.OK_OPTION) {
						clearImagesPanel();
						int[] foundIds = searchByAuthor(authorField.getText());
						for(int i = 0; i < foundIds.length; i++) {
							if(foundIds[i] == 1) {
								foundImage = images.get(i);
								foundImage.show();
							}
						}
					}
					break;
				case "Miejsce":
					JPanel searchByPlacePanel = new JPanel();
					JTextField placeField = new JTextField(15);
					searchByPlacePanel.add(placeField);
					int resultNext3 = JOptionPane.showConfirmDialog(null, searchByPlacePanel,
							"Wpisz miejsce do wyszukania", JOptionPane.OK_CANCEL_OPTION);
					if(resultNext3 == JOptionPane.OK_OPTION) {
						clearImagesPanel();
						int[] foundIds = searchByPlace(placeField.getText());
						for(int i = 0; i < foundIds.length; i++) {
							if(foundIds[i] == 1) {
								foundImage = images.get(i);
								foundImage.show();
							}
						}
					}
					break;
				case "Data":
					JPanel searchByDatePanel = new JPanel();
					searchByDatePanel.setPreferredSize(new Dimension(300, 50));
					JTextField dateField = new JTextField(15);
					searchByDatePanel.add(dateField);
					int resultNext4 = JOptionPane.showConfirmDialog(null, searchByDatePanel,
							"Wpisz datê do wyszukania (format: dd.mm.rrrr)", JOptionPane.OK_CANCEL_OPTION);
					if(resultNext4 == JOptionPane.OK_OPTION) {
						clearImagesPanel();
						int[] foundIds = searchByDate(dateField.getText());
						for(int i = 0; i < foundIds.length; i++) {
							if(foundIds[i] == 1) {
								foundImage = images.get(i);
								foundImage.show();
							}
						}
					}
					break;
				case "Tagi":
					JPanel searchByTagsPanel = new JPanel();
					searchByTagsPanel.setPreferredSize(new Dimension(750, 50));
					JTextField tagsField = new JTextField(1000);
					searchByTagsPanel.add(tagsField);
					int resultNext5 = JOptionPane.showConfirmDialog(null, searchByTagsPanel,
							"Wpisz tagi do wyszukania (format: tag1, tag2, tag3...)", JOptionPane.OK_CANCEL_OPTION);
					if(resultNext5 == JOptionPane.OK_OPTION) {
						clearImagesPanel();
						int[] foundIds = searchByTags(tagsField.getText());
						for(int i = 0; i < foundIds.length; i++) {
							if(foundIds[i] == 1) {
								foundImage = images.get(i);
								foundImage.show();
							}
						}
					}
					break;
				}
				reloadGUI();
			}
		});
		JButton sortByParameterButton = new JButton("Sortuj rosn¹co wed³ug...");
		sortByParameterButton.addActionListener(event -> {
			String[] parametersTab = new String[] {"Autor", "Miejsce", "Data"};
			JComboBox<String> chooseParameterBox = new JComboBox<>(parametersTab);
			
			JPanel chooseParameterPanel = new JPanel();
			chooseParameterPanel.add(chooseParameterBox);
			chooseParameterPanel.setPreferredSize(new Dimension(200, 50));
			
			int result = JOptionPane.showConfirmDialog(null, chooseParameterPanel,
					"Wybierz parametr", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				String chosenParameter = (String)chooseParameterBox.getSelectedItem();
				switch(chosenParameter) {
				case "Autor":
					clearImagesPanel();
					sortByAuthor();
					showAll();
					break;
				case "Miejsce":
					clearImagesPanel();
					sortByPlace();
					showAll();
					break;
				case "Data":
					clearImagesPanel();
					sortByDate();
					showAll();
					break;
				}
				reloadGUI();
			}
		});
		JButton sortDescByParameterButton = new JButton("Sortuj malej¹co wed³ug...");
		sortDescByParameterButton.addActionListener(event -> {
			String[] parametersTab = new String[] {"Autor", "Miejsce", "Data"};
			JComboBox<String> chooseParameterBox = new JComboBox<>(parametersTab);
			
			JPanel chooseParameterPanel = new JPanel();
			chooseParameterPanel.add(chooseParameterBox);
			chooseParameterPanel.setPreferredSize(new Dimension(200, 50));
			
			int result = JOptionPane.showConfirmDialog(null, chooseParameterPanel,
					"Wybierz parametr", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				String chosenParameter = (String)chooseParameterBox.getSelectedItem();
				switch(chosenParameter) {
				case "Autor":
					clearImagesPanel();
					sortDescByAuthor();
					showAll();
					break;
				case "Miejsce":
					clearImagesPanel();
					sortDescByPlace();
					showAll();
					break;
				case "Data":
					clearImagesPanel();
					sortDescByDate();
					showAll();
					break;
				}
				reloadGUI();
			}
		});
		JButton searchLeastButton = new JButton("Wyszukaj najmniejszy...");
		searchLeastButton.addActionListener(event -> {
			String[] parametersTab = new String[] {"Autor", "Miejsce", "Data"};
			JComboBox<String> chooseParameterBox = new JComboBox<>(parametersTab);
			
			JPanel chooseParameterPanel = new JPanel();
			chooseParameterPanel.add(chooseParameterBox);
			chooseParameterPanel.setPreferredSize(new Dimension(200, 50));
			
			int result = JOptionPane.showConfirmDialog(null, chooseParameterPanel,
					"Wybierz parametr", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				String chosenParameter = (String)chooseParameterBox.getSelectedItem();
				switch(chosenParameter) {
				case "Autor":
					clearImagesPanel();
					getLeastByAuthor();
					break;
				case "Miejsce":
					clearImagesPanel();
					getLeastByPlace();
					break;
				case "Data":
					clearImagesPanel();
					getLeastByDate();
					break;
				}
				reloadGUI();
			}
		});
		JButton searchLargestButton = new JButton("Wyszukaj najwiêkszy...");
		searchLargestButton.addActionListener(event -> {
			String[] parametersTab = new String[] {"Autor", "Miejsce", "Data"};
			JComboBox<String> chooseParameterBox = new JComboBox<>(parametersTab);
			
			JPanel chooseParameterPanel = new JPanel();
			chooseParameterPanel.add(chooseParameterBox);
			chooseParameterPanel.setPreferredSize(new Dimension(200, 50));
			
			int result = JOptionPane.showConfirmDialog(null, chooseParameterPanel,
					"Wybierz parametr", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				String chosenParameter = (String)chooseParameterBox.getSelectedItem();
				switch(chosenParameter) {
				case "Autor":
					clearImagesPanel();
					getLargestByAuthor();
					break;
				case "Miejsce":
					clearImagesPanel();
					getLargestByPlace();
					break;
				case "Data":
					clearImagesPanel();
					getLargestByDate();
					break;
				}
				reloadGUI();
			}
		});
		JButton searchEarlierButton = new JButton("Wyszukaj wczeœniejsze ni¿...");
		searchEarlierButton.addActionListener(event -> {
			JPanel searchEarlierPanel = new JPanel();
			JTextField dateField = new JTextField(15);
			searchEarlierPanel.add(dateField);
			
			int result = JOptionPane.showConfirmDialog(null, searchEarlierPanel,
					"Wpisz datê (format: dd.mm.rrrr)", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				showEarlierThanDate(dateField.getText());
				reloadGUI();
			}
		});
		JButton searchLaterButton = new JButton("Wyszukaj póŸniejsze ni¿...");
		searchLaterButton.addActionListener(event -> {			
			JPanel searchLaterPanel = new JPanel();
			JTextField dateField = new JTextField(15);
			searchLaterPanel.add(dateField);
			
			int result = JOptionPane.showConfirmDialog(null, searchLaterPanel,
					"Wpisz datê (format: dd.mm.rrrr)", JOptionPane.OK_CANCEL_OPTION);
			
			if(result == JOptionPane.OK_OPTION) {
				showLaterThanDate(dateField.getText());
				reloadGUI();
			}
		});
		JButton saveDBasButton = new JButton("Zapisz bazê jako...");
		saveDBasButton.addActionListener(event -> {
			JFileChooser fileToSaveChooser = new JFileChooser();
			FileNameExtensionFilter restrict = new FileNameExtensionFilter("Tylko pliki .txt!", "txt");
			fileToSaveChooser.addChoosableFileFilter(restrict);
			fileToSaveChooser.setAcceptAllFileFilterUsed(false); 
			
			String customPath = new String();
			
			int result = fileToSaveChooser.showSaveDialog(Utils.mainFrame);
			if(result == JFileChooser.APPROVE_OPTION) {
				Pattern fileValidationPattern = Pattern.compile("(.*)\\.txt");
				customPath = fileToSaveChooser.getSelectedFile().getAbsolutePath();
				Matcher fileValidationMatcher = fileValidationPattern.matcher(customPath);
				if(fileValidationMatcher.matches() != true) {
					customPath += ".txt";
				}
			}
			
			if(result == JOptionPane.OK_OPTION) {
				saveDB(customPath);
			}
		});
		menuPanel.add(addImageButton);
		menuPanel.add(editImageButton);
		menuPanel.add(removeImageButton);
		menuPanel.add(showSingleImgButton);
		menuPanel.add(showAllButton);
		menuPanel.add(searchByTagButton);
		menuPanel.add(searchByParameterButton);
		menuPanel.add(sortByParameterButton);
		menuPanel.add(sortDescByParameterButton);
		menuPanel.add(searchLeastButton);
		menuPanel.add(searchLargestButton);
		menuPanel.add(searchEarlierButton);
		menuPanel.add(searchLaterButton);
		menuPanel.add(saveDBasButton);
		menuPanel.setPreferredSize(new Dimension(screenDim.width, 75));
		menuPanel.setBackground(new Color(53, 81, 92));
		
		mainFrame.add(menuPanel, BorderLayout.NORTH);
	}
	
	public static void correctIds() {
		for(int i = 0; i < images.size(); i++) {
			Utils.images.get(i).setId(i);
		}
	}
	
	public static void readDB() {
		try {
			Scanner stream = new Scanner(database, "UTF-8");
			
			while(stream.hasNextLine()) {
				String imageInfo = stream.nextLine();
				String imageInfoSplitted[] = new String[5];
				
				imageInfoSplitted = imageInfo.split(" \\| ");
				
				int[] dateSplitted = splitDate(imageInfoSplitted[3]);
				
				Image img = null;
				
				try {
					img = new Image(imagesCount, imageInfoSplitted[0], imageInfoSplitted[1], imageInfoSplitted[2], dateSplitted[0], dateSplitted[1], dateSplitted[2], imageInfoSplitted[4].split(", "));
				} catch (NumberFormatException e) {
					img = new Image(imagesCount, imageInfoSplitted[0], imageInfoSplitted[1], imageInfoSplitted[2], -1, -1, -1, imageInfoSplitted[4].split(", "));

				}
				
				images.add(img);				
				imagesCount = images.size();
			}
			
			stream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveDB(String path) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));			
			Image tempImg = null;
			for(int i = 0; i < imagesCount; i++) {
				tempImg = images.get(i);
				writer.write(tempImg.dir + " | " + tempImg.author + " | " + tempImg.place + " | ");
				if(tempImg.day < 10) {
					writer.write("0" + tempImg.day + ".");
				} else {
					writer.write(tempImg.day + ".");
				}
				if(tempImg.month < 10) {
					writer.write("0" + tempImg.month + ".");
				} else {
					writer.write(tempImg.month + ".");
				}
				writer.write(tempImg.year + " | ");
				for(int j = 0; j < tempImg.tags.length-1; j++) {
					writer.write(tempImg.tags[j] + ", ");
				}
				writer.write(tempImg.tags[tempImg.tags.length-1]);
				
				if(i != imagesCount-1)
					writer.write("\n");
			}
			
			System.out.println("Database saved.");
			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void showAll() {
		clearImagesPanel();
		imagesPanel.setBackground(new Color(25, 25, 25));
		imagesPanel.setPreferredSize(new Dimension(1500, 750));
		
		FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 25, 25);
		
		imagesPanel.setLayout(layout);
		
		mainFrame.add(imagesPanel, BorderLayout.CENTER);
				
		for(int i = 0; i < imagesCount; i++) {
			images.get(i).show();
			
			System.out.println(images.get(i));
			
			reloadGUI();
		}
	}
	
	public static void showByTag(String tag) {
		clearImagesPanel();
		for(int i = 0; i < imagesCount; i++) {
			for(int j = 0; j < images.get(i).tags.length; j++) {
				if(images.get(i).tags[j].equals(tag)) {
					images.get(i).show();
					break;
				}
			}
		}
	}
	
	
	// SEARCHING
	public static int searchByDir(String dir) {
		for(Image i : images)
			if(i.getDir().equals(dir)) {
				return i.getId();
			}
		return -1;
	}
	
	public static int[] searchByAuthor(String author) {
		int[] foundIds = new int[images.size()];
		for(Image i : images)
			if(i.getAuthor().equals(author)) {
				foundIds[i.getId()] = 1;
			}
		return foundIds;
	}
	
	public static int[] searchByPlace(String place) {
		int[] foundIds = new int[images.size()];
		for(Image i : images)
			if(i.getPlace().equals(place)) {
				foundIds[i.getId()] = 1;
			}
		return foundIds;
	}
	
	public static int[] searchByDate(String date) {
		int[] foundIds = new int[images.size()];
		for(Image i : images)
			if(i.getDate().equals(date)) {
				foundIds[i.getId()] = 1;
			}
		return foundIds;
	}
	
	public static int[] searchByTags(String tags) {
		int[] foundIds = new int[images.size()];
		for(Image i : images)
			if(i.getTags().equals(tags)) {
				foundIds[i.getId()] = 1;
			}
		return foundIds;
	}
	
	
	// SORTING
	public static void sortByAuthor() {
		authorComparator comparator = new authorComparator();
		
		Collections.sort(images, comparator);
	}
	
	public static void sortByPlace() {
		placeComparator comparator = new placeComparator();
		
		Collections.sort(images, comparator);
	}
	
	public static void sortByDate() {
		dateComparator comparator = new dateComparator();
		
		Collections.sort(images, comparator);
	}
	
	
	public static void sortDescByAuthor() {
		authorComparator comparator = new authorComparator();
		
		Collections.sort(images, comparator);
		Collections.reverse(images);
	}
	
	public static void sortDescByPlace() {
		placeComparator comparator = new placeComparator();
		
		Collections.sort(images, comparator);
		Collections.reverse(images);
	}
	
	public static void sortDescByDate() {
		dateComparator comparator = new dateComparator();
		
		Collections.sort(images, comparator);
		Collections.reverse(images);
	}
	
	
	// DISPLAY THE LEAST AND LARGEST ELEMENT BY PARAMETER
	public static void getLeastByAuthor() {
		clearImagesPanel();
		
		Image least = images.get(0);
		
		for(int i = 1; i < images.size(); i++) {
			if(images.get(i).author.compareToIgnoreCase(least.author) < 0) {
				least = images.get(i);
			}
		}
		
		least.show();
		reloadGUI();
	}
	
	public static void getLargestByAuthor() {
		clearImagesPanel();
		
		Image largest = images.get(0);
		
		for(int i = 1; i < images.size(); i++) {
			if(images.get(i).author.compareToIgnoreCase(largest.author) > 0) {
				largest = images.get(i);
			}
		}
		
		largest.show();
		reloadGUI();
	}
	
	
	
	public static void getLeastByPlace() {
		clearImagesPanel();
		
		Image least = images.get(0);
		
		for(int i = 1; i < images.size(); i++) {
			if(images.get(i).place.compareToIgnoreCase(least.place) < 0) {
				least = images.get(i);
			}
		}
		
		least.show();
		reloadGUI();	
	}
	
	public static void getLargestByPlace() {
		clearImagesPanel();
		
		Image largest = images.get(0);
		
		for(int i = 1; i < images.size(); i++) {
			if(images.get(i).place.compareToIgnoreCase(largest.place) > 0) {
				largest = images.get(i);
			}
		}
		
		largest.show();
		reloadGUI();
	}
	
	
	
	public static void getLeastByDate() {
		clearImagesPanel();
		
		Image least = images.get(0);
		
		for(int i = 1; i < images.size(); i++) {
			if(images.get(i).year < least.year)
				least = images.get(i);
			else if(images.get(i).year == least.year && images.get(i).month < least.month)
				least = images.get(i);
			else if(images.get(i).month == least.month && images.get(i).day < least.day)
				least = images.get(i);
		}
		
		least.show();
		reloadGUI();
	}
	
	public static void getLargestByDate() {
		clearImagesPanel();
		
		Image largest = images.get(0);
		
		for(int i = 1; i < images.size(); i++) {
			
			if(images.get(i).year > largest.year)
				largest = images.get(i);
			
			if(images.get(i).year == largest.year && images.get(i).month > largest.month)
				largest = images.get(i);
			
			if(images.get(i).year == largest.year && images.get(i).month == largest.month && images.get(i).day > largest.day)
				largest = images.get(i);
		}
		
		largest.show();
		reloadGUI();
	}
	
	
	// Earlier and Later Image by Date given from User
	public static void showEarlierThanDate(String date) {
		clearImagesPanel();
		int[] dateSplitted = splitDate(date);
		for(Image i : images) {
			if(i.year < dateSplitted[2]) {
				i.show();
			} else if(i.year == dateSplitted[2]) {
				if(i.month < dateSplitted[1]) {
					i.show();
				} else if(i.month == dateSplitted[1]) {
					if(i.day < dateSplitted[0]) {
						i.show();
					}
				}
			}
		}
	}
	
	public static void showLaterThanDate(String date) {
		clearImagesPanel();
		int[] dateSplitted = splitDate(date);
		for(Image i : images) {
			if(i.year > dateSplitted[2]) {
				i.show();
			} else if(i.year == dateSplitted[2]) {
				if(i.month > dateSplitted[1]) {
					i.show();
				} else if(i.month == dateSplitted[1]) {
					if(i.day > dateSplitted[0]) {
						i.show();
					}
				}
			}
		}
	}
	
	public static int[] splitDate(String date) {
		String[] dateSplitted = new String[3];
		int[] dateSplittedInts = new int[3];
		
		try {
			dateSplitted = date.split("\\.");
			dateSplittedInts[0] = Integer.parseInt(dateSplitted[0]);
			dateSplittedInts[1] = Integer.parseInt(dateSplitted[1]);
			dateSplittedInts[2] = Integer.parseInt(dateSplitted[2]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		
		return dateSplittedInts;
	}
}
