package net.isksss.mc.minegacha;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
public class GachaCommand implements CommandExecutor {

    private final int MIN_LEVEL=100;
    private final List<ItemStack> itemList = new ArrayList<>();
    private final List<Integer> itemWeights = new ArrayList<>();
    private Random random = new Random();

    public GachaCommand(){
        addItemWithWeight(new ItemStack(Material.ELYTRA,1), 1);
        addItemWithWeight(new ItemStack(Material.NETHERITE_INGOT,2), 2);
        addItemWithWeight(new ItemStack(Material.DIAMOND,4), 3);
        addItemWithWeight(new ItemStack(Material.GOLD_INGOT,16), 4);
        addItemWithWeight(new ItemStack(Material.LAPIS_LAZULI,16), 5);
        addItemWithWeight(new ItemStack(Material.EXPERIENCE_BOTTLE,16), 6);
        addItemWithWeight(new ItemStack(Material.EXPERIENCE_BOTTLE,8), 7);
    }

    // アイテムとその重みをリストに追加するメソッド
    private void addItemWithWeight(ItemStack item, int weight) {
        itemList.add(item);
        itemWeights.add(weight);
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {

        if(!command.getName().equalsIgnoreCase("gacha")){
            return false;
        }

        if(!(sender instanceof Player p)){
            sender.sendMessage("not player");
            return false;
        }

        int level = p.getLevel(); // 現在のレベル

        // 最低レベルをクリアしていない場合
        if (level < MIN_LEVEL){
            p.sendMessage("The required level is not enough.");
            p.sendMessage("You can run gacha from "+MIN_LEVEL+" levels.");
            return false;
        }

        // インベントリに空きがあるか確認
        if (!hasEmptySlot(p)){
            p.sendMessage("Inventory is full.");
            p.sendMessage("Keep your items organized.");
            return false;
        }

        // アイテムをインベントリに追加
        addItemToInventory(p);

        return true;
    }

    // プレイヤーのインベントリに空きがあるかどうかを確認する関数
    public boolean hasEmptySlot(Player player) {
        Inventory inventory = player.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (item == null) {
                return true; // 空きがある場合
            }
        }

        return false; // 空きがない場合
    }

    // 現在のプレイヤーのレベルから必要レベルを引く関数
    public void subtractLevels(Player player) {
        int currentLevel = player.getLevel();

        if (currentLevel >= MIN_LEVEL) {
            player.setLevel(currentLevel - MIN_LEVEL);
        } else {
            player.setLevel(0);
        }
    }

    // 重みに基づいてランダムにアイテムを選ぶメソッド
    private ItemStack getRandomWeightedItem() {
        int totalWeight = itemWeights.stream().mapToInt(Integer::intValue).sum();
        int randomWeight = random.nextInt(totalWeight) + 1;

        int cumulativeWeight = 0;
        for (int i = 0; i < itemList.size(); i++) {
            cumulativeWeight += itemWeights.get(i);
            if (randomWeight <= cumulativeWeight) {
                return itemList.get(i);
            }
        }

        // リストにアイテムがない場合やエラーの場合はnullを返す
        return null;
    }

    // プレイヤーのインベントリにアイテムを追加する関数
    private void addItemToInventory(Player player) {
        ItemStack randomItem = getRandomWeightedItem();

        if (randomItem != null) {
            Inventory inventory = player.getInventory();

            // インベントリに空きがあるか確認
            if (hasEmptySlot(player)) {
                // プレイヤーから必要レベルを減らす
                subtractLevels(player);

                inventory.addItem(randomItem);
                player.sendMessage("You got an item: " + randomItem.getType());
            } else {
                player.sendMessage("Inventory is full. Keep your items organized. Inventory is full. Keep your items organized.");
            }
        } else {
            player.sendMessage("The item could not be retrieved.");
        }
    }

}
