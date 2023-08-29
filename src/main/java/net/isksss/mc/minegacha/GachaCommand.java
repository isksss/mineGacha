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
        // アイテムと重みをリストに追加
        int WEIGHT_1 = 3;
        int WEIGHT_2 = 5;
        int WEIGHT_3 = 10;
        int WEIGHT_4 = 15;
        int WEIGHT_5 = 20;
        addItemWithWeight(new ItemStack(Material.ELYTRA,1), WEIGHT_1);
        addItemWithWeight(new ItemStack(Material.NETHERITE_INGOT,3), WEIGHT_1);

        addItemWithWeight(new ItemStack(Material.NETHERITE_INGOT,1), WEIGHT_2);
        addItemWithWeight(new ItemStack(Material.DIAMOND,8), WEIGHT_2);

        addItemWithWeight(new ItemStack(Material.DIAMOND,4), WEIGHT_3);
        addItemWithWeight(new ItemStack(Material.GOLD_INGOT,16), WEIGHT_3);
        addItemWithWeight(new ItemStack(Material.EXPERIENCE_BOTTLE,32), WEIGHT_3);

        addItemWithWeight(new ItemStack(Material.LAPIS_LAZULI,32), WEIGHT_4);
        addItemWithWeight(new ItemStack(Material.IRON_INGOT, 64), WEIGHT_4);

        addItemWithWeight(new ItemStack(Material.EXPERIENCE_BOTTLE,8), WEIGHT_5);
        addItemWithWeight(new ItemStack(Material.EXPERIENCE_BOTTLE,16), WEIGHT_5);
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
            p.sendMessage("必要レベルが足りていません。");
            p.sendMessage("100レベルからガチャを実行できます。");
            return false;
        }

        // インベントリに空きがあるか確認
        if (!hasEmptySlot(p)){
            p.sendMessage("インベントリに空きがありません。");
            p.sendMessage("アイテムを整理してください。");
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
                player.sendMessage("アイテムをゲットしました: " + randomItem.getType());
            } else {
                player.sendMessage("インベントリに空きがありません。アイテムを整理してください。");
            }
        } else {
            player.sendMessage("アイテムが取得できませんでした。");
        }
    }

}
