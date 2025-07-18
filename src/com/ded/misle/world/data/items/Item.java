package com.ded.misle.world.data.items;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.logic.TurnTimer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;

import static com.ded.misle.core.Path.getPath;
import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.boxes.BoxHandling.addBoxItem;

public class Item {
	private final int id;
	private int countLimit;
	private BufferedImage icon;
	private int count;
	private boolean active;
	private double animationRotation;
	private double animationX;
	private double animationY;
	private double animationBulk;
	private String displayName;
	private final String name;
	private final String displayType;
	private final String description;
	private final String rarity;
	private final String type;
	private final Map<String, Object> attributes; // Holds dynamic attributes
	private final Color nameColor;
	private final String displayEffect;

	private TurnTimer usageDelay;
	private boolean canUse = true;

	HashMap<Integer, BufferedImage> iconCache = new HashMap<>();

	// Constructor that takes only ID and sets default count to 1
	public Item(int id) throws Exception {
		this.id = id;

		// Load item details from ItemLoader
		ItemData itemDetails = ItemLoader.loadItemDataById(id);
		if (itemDetails != null) {
			this.name = itemDetails.getName();
			String normalizedName = itemDetails.getName()
				.replaceAll(" ", "_")
				.toLowerCase();
			this.displayName = LanguageManager.getText(normalizedName);
			this.description = LanguageManager.getText(normalizedName + "_DESC");
			this.countLimit = itemDetails.countLimit();
			this.rarity = itemDetails.getRarity();
			this.type = itemDetails.getType();
			this.displayType = LanguageManager.getText("TYPE_" + itemDetails.getType());
			this.displayEffect = LanguageManager.getText(normalizedName + "_EFFECT");
			this.attributes = itemDetails.getAttributes();
			this.active = true;
			this.nameColor = itemDetails.getNameColor();
			this.animationRotation = 0;
			this.animationX = 0;
			this.animationY = 0;
			this.animationBulk = 1;
			if (iconCache.containsKey(id)) {
				this.icon = iconCache.get(id);
			} else {
				Path basePath = getPath(com.ded.misle.core.Path.PathTag.RESOURCES).resolve("images/items");
				Path filePath = basePath.resolve(itemDetails.getResourceID() + ".png");

				try {
					this.icon = ImageIO.read(filePath.toFile());
					iconCache.put(id, this.icon);
				} catch (IOException e) {
					System.out.println("Can't find item texture " + filePath + "!");
					try {
						this.icon = ImageIO.read(basePath.resolve(1 + ".png").toFile());
						iconCache.put(id, this.icon);
					} catch (IOException exc) {
						System.out.println("Can't find base item texture " + filePath + "!");
						this.icon = null;
					}
				}
			}

		} else {
			System.err.println("Item with ID " + id + " not found.");
			name = "";
			displayType = "";
			description = "";
			rarity = "";
			type = "";
			attributes = new HashMap<>();
			nameColor = null;
			displayEffect = "";
			this.active = false;
		}

		this.count = 1; // Default count is set to 1
	}

	public Item(int id, int count) throws Exception {
		this(id); // Call the first constructor
		this.count = count; // Set count
	}

	public int getId() { return id; }
	public String getName() { return name; }
	public String getDisplayName() { return displayName; }
	public void setDisplayName(String displayName) { this.displayName = displayName; }
	public String getDescription() { return description; }
	public int getCountLimit() { return countLimit; }
	public String getRarity() { return rarity; }
	public String getType() { return type; }
	public String getSubtype() { try { return (String) this.getAttributes().get("subtype"); } catch (NullPointerException e) { return "";} }
	public String getDisplayType() { return displayType; }
	public String getDisplayEffect() { return displayEffect; }
	public Map<String, Object> getAttributes() { return attributes; }
	public int getCount() { return count; }
	public boolean isActive() { return active; }
	public Color getNameColor() { return nameColor; }
	public TurnTimer getUsageDelay() { return usageDelay; }
	public void setUsageDelay(TurnTimer usageDelay) {
		this.usageDelay = usageDelay;
		this.usageDelay.restart();
	}
	public boolean canUse() { return canUse; }
	public void setCanUse(boolean canUse) { this.canUse = canUse; }

	public boolean setCount(int count) {
		this.count = Math.min(count, countLimit);
		updateMaxStackSize();
		if (this.count <= 0) {
			removeItem();
			return true;
		}
		return false;
	}

	public void removeItem() {
		this.active = false; // Mark the item as inactive
		// Additional cleanup actions (e.g., removing from lists, notifying other objects)
	}

	@Override
	public String toString() {
		return "Item{id=" + id + ", name='" + displayName + "', description='" + description + "', countLimit=" + countLimit +", rarity='" + rarity + "', type='" + type + "', attributes=" + attributes + ", count=" + count + "}";
	}

	public static Item createItem(int id) {
		try {
			return new Item(id); // Attempt to create an Item
		} catch (Exception e) {
			// Handle the exception
			System.err.println(e.getMessage());
			return null; // or return no Item whatsoever
		}
	}

	public static Item createItem(int id, int Count) {
		try {
			return new Item(id, Count); // Attempt to create an Item
		} catch (Exception e) {
			return null; // or return a default Item
		}
	}

	public BufferedImage getIcon() {
		return this.icon;
	}

	public void setIcon(int resourceID) {
		if (iconCache.containsKey(id)) {
			this.icon = iconCache.get(id);
		} else {
			Path basePath = getPath(com.ded.misle.core.Path.PathTag.RESOURCES).resolve("images/items");
			Path filePath = basePath.resolve(resourceID + ".png");

			try {
				this.icon = ImageIO.read(filePath.toFile());
				iconCache.put(id, this.icon);
			} catch (IOException e) {
				System.out.println("Can't find item texture " + filePath + "!");
				try {
					this.icon = ImageIO.read(basePath.resolve(1 + ".png").toFile());
					iconCache.put(id, this.icon);
				} catch (IOException exc) {
					System.out.println("Can't find base item texture " + filePath + "!");
					this.icon = null;
				}
			}
		}
	}

	public static Box createDroppedItem(int x, int y, int id, int count) {
		return addBoxItem(x, y, id, count);
	}

	// ANIMATION STUFF

	public double getAnimationRotation() {
		return this.animationRotation;
	}

	public void setAnimationRotation(double animationRotation) {
		this.animationRotation = animationRotation;
	}

	public void delayedSetAnimationRotation(double animationRotation, double delay) {
		int frames = (int) (delay / 1000 * 60);
		double dRotation = animationRotation / (double) frames;

		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					setAnimationRotation(getAnimationRotation() + dRotation);
					count++;
				} else {
					((Timer) evt.getSource()).stop();  // Stop the timer when done
				}
			}
		});
		timer.start();
	}

	public void setAnimationX(double animationX) {
		this.animationX = animationX;
	}

	public void delayedMoveAnimationX(double animationX, double delay) {
		int frames = (int) (delay / 1000 * 60);
		double dx = animationX / (double) frames;

		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					setAnimationX(getAnimationX() + dx);
					count++;
				} else {
					((Timer) evt.getSource()).stop();  // Stop the timer when done
				}
			}
		});
		timer.start();
	}

	public void setAnimationY(double animationY) {
		this.animationY = animationY;
	}

	public void delayedMoveAnimationY(double animationY, double delay) {
		int frames = (int) (delay / 1000 * 60);
		double dy = animationY / (double) frames;

		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					setAnimationY(getAnimationY() + dy);
					count++;
				} else {
					((Timer) evt.getSource()).stop();  // Stop the timer when done
				}
			}
		});
		timer.start();
	}

	public double getAnimationX() {
		return this.animationX;
	}

	public double getAnimationY() {
		return this.animationY;
	}

	public double getAnimationBulk() {
		return this.animationBulk;
	}

	public void setAnimationBulk(double animationBulk) {
		this.animationBulk = animationBulk;
	}

	public void delayedChangeAnimationBulk(double animationBulk, double delay) {
		int frames = (int) (delay / 1000 * 60);
		double dBulk = animationBulk / (double) frames;

		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					setAnimationBulk(getAnimationBulk() + dBulk);
					count++;
				} else {
					((Timer) evt.getSource()).stop();  // Stop the timer when done
				}
			}
		});
		timer.start();
	}

	public void resetAnimation() {
		this.animationRotation = 0;
		this.animationX = 0;
		this.animationY = 0;
		this.animationBulk = 1;
	}

	// COUNT LIMIT

	public void updateMaxStackSize() {
		if (this.getCount() > 1) {
			this.countLimit = (int) (this.countLimit * player.attr.getMaxStackSizeMulti());
			if (this.getCount() > this.countLimit) {
				this.setCount(this.countLimit);
			}
		}
	}

	public static void updateInventoryMaxStackSize() {
		if (player == null || player.inv == null) return;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 7; j++) {
				Item item = player.inv.getItem(i, j);
				if (item != null) {
					item.updateMaxStackSize();
				}
			}
		}
	}
}
