import java.util.Arrays;
import java.util.Scanner;

class Menu {

    String Name;
    double Price;
    String Category;

    public Menu(String Name, double Price, String Category) {
        this.Name = Name;
        this.Price = Price;
        this.Category = Category;
    }

    public String getName() {
        return Name;
    }

    public double getPrice() {
        return Price;
    }

    public String getCategory() {
        return Category;
    }

    static Menu[] menuList = {
            new Menu("Nasi Ayam", 12000, "makanan"),
            new Menu("Nasi Telur", 10000, "makanan"),
            new Menu("Nasi Rendang", 13000, "makanan"),
            new Menu("Nasi Gulai", 15000, "makanan"),
            new Menu("Es Jeruk", 5000, "minuman"),
            new Menu("Es Teh", 3000, "minuman"),
            new Menu("Es Teler", 8000, "minuman"),
            new Menu("Es Kopi", 7000, "minuman"),
    };

    public static void displayMenu() {
        System.out.println("                               ");
        System.out.println("    ##### MENU MAKANAN ####    ");
        System.out.println("                               ");
        Arrays.stream(menuList)
                .filter(menu -> menu.getCategory().equals("makanan"))
                .forEach(menu -> System.out.println(menu.getName() + " - Rp " + (int)menu.getPrice()));

        System.out.println("                               ");
        System.out.println("    ##### MENU MINUMAN ####    ");
        System.out.println("                               ");
        Arrays.stream(menuList)
                .filter(menu -> menu.getCategory().equals("minuman"))
                .forEach(menu -> System.out.println(menu.getName() + " - Rp " + (int)menu.getPrice()));
    }

}

public class Main {
    static final double SERVICE_CHARGE = 20000;
    static final double TAX_RATE = 0.1;
    static final double DISCOUNT_THRESHOLD = 100000;
    static final double DISCOUNT_RATE = 0.1;
    static final double MaxB1g1 = 50000;
    public static double TotalDisc = 0;
    public static long FreeDrinkQty = 0;

    public static double calculateTotalCost(String[] orders) {

        double totalCost = Arrays.stream(orders)
                .filter(order -> order != null)
                .mapToDouble(order -> {
                    String[] parts = order.split("=");
                    String itemName = parts[0].trim();
                    int quantity = Integer.parseInt(parts[1].trim());
                    return Arrays.stream(Menu.menuList)
                            .filter(menu -> menu.getName().equalsIgnoreCase(itemName))
                            .mapToDouble(menu -> menu.getPrice() * quantity)
                            .sum();
                })
                .sum();

        long totalQuantity = Arrays.stream(orders)
                .filter(order -> order != null)
                .mapToLong(order -> {
                    String[] parts = order.split("=");
                    return Long.parseLong(parts[1].trim());
                })
                .sum();

        double totalBeforeTaxAndServiceCharge = totalCost;

        //Diskon 10% jika total > Rp 100.000
        if (totalBeforeTaxAndServiceCharge > DISCOUNT_THRESHOLD) {
            TotalDisc = totalBeforeTaxAndServiceCharge * DISCOUNT_RATE;
            totalCost -= TotalDisc;
        }

        //Promo B1G1 untuk minuman jika total >= Rp 50.000
        if (totalBeforeTaxAndServiceCharge >= MaxB1g1) {
            long drinkQuantity = Arrays.stream(orders)
                    .filter(order -> order != null)
                    .mapToLong(order -> {
                        String[] parts = order.split("=");
                        String itemName = parts[0].trim();
                        int quantity = Integer.parseInt(parts[1].trim());
                        return Arrays.stream(Menu.menuList)
                                .filter(menu -> menu.getName().equalsIgnoreCase(itemName) 
                                        && menu.getCategory().equalsIgnoreCase("minuman"))
                                .mapToLong(menu -> quantity)
                                .sum();
                    })
                    .sum();

            if (drinkQuantity > 0) {
                FreeDrinkQty = drinkQuantity / 2;
                System.out.println("\n  SELAMAT! Anda mendapat Promo Beli 1 Gratis 1!");
                System.out.println("   Bonus: " + FreeDrinkQty + " minuman akan ditambahkan GRATIS");
            }
        }

        double tax = totalCost * TAX_RATE;
        totalCost += tax + SERVICE_CHARGE;

        System.out.println("\n===============================================");
        System.out.println("            RINGKASAN PEMBAYARAN");
        System.out.println("===============================================");
        System.out.println("Total Pesanan              : " + totalQuantity + " item");
        System.out.println("Subtotal Awal              : Rp " + (int)totalBeforeTaxAndServiceCharge);
        if (TotalDisc > 0) {
            System.out.println("Potongan Diskon (10%)      : Rp " + (int)TotalDisc);
            System.out.println("Subtotal Setelah Diskon    : Rp " + (int)(totalBeforeTaxAndServiceCharge - TotalDisc));
        }
        System.out.println("Pajak (PPN 10%)            : Rp " + (int)tax);
        System.out.println("Biaya Pelayanan            : Rp " + (int)SERVICE_CHARGE);
        System.out.println("-----------------------------------------------");

        return totalCost;
    }

    public static void printReceipt(String[] orders, double totalCost) {
        System.out.println("\n===============================================");
        System.out.println("                STRUK PEMBAYARAN");
        System.out.println("===============================================");
        System.out.println("\nPESANAN ANDA:");
        System.out.println("-----------------------------------------------");
        
        int index = 0;
        printReceiptRecursive(orders, totalCost, index);

        if (FreeDrinkQty > 0) {
            System.out.println("\n-----------------------------------------------");
            System.out.println("   BONUS PROMO BELI 1 GRATIS 1:");
            System.out.println("   Anda mendapat " + FreeDrinkQty + " minuman GRATIS!");
            System.out.println("   (Minuman bonus sesuai dengan pesanan Anda)");
        }

        System.out.println("\n===============================================");
        System.out.println("TOTAL YANG HARUS DIBAYAR : Rp " + (int)totalCost);
        System.out.println("===============================================\n");
    }

    private static void printReceiptRecursive(String[] orders, double totalCost, int index) {
        if (index < 0 || index >= orders.length || orders[index] == null) {
            return;
        }

        String[] parts = orders[index].split("=");

        if (parts.length != 2) {
            System.out.println("Invalid order format = " + orders[index]);
            return;
        }

        String itemName = parts[0].trim();
        int quantity;

        try {
            quantity = Integer.parseInt(parts[1].trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity format = " + orders[index]);
            return;
        }

        printItemReceipt(itemName, quantity);
        printReceiptRecursive(orders, totalCost, index + 1);
    }

    private static void printItemReceipt(String itemName, int quantity) {
        Arrays.stream(Menu.menuList)
                .filter(menu -> menu.getName().equalsIgnoreCase(itemName))
                .findFirst()
                .ifPresent(menu -> {
                    double itemTotal = menu.getPrice() * quantity;
                    System.out.printf("%-25s x%-3d\n", menu.getName(), quantity);
                    System.out.printf("  @Rp %-20d = Rp %d\n", (int)menu.getPrice(), (int)itemTotal);
                    System.out.println();
                });
    }

    static final int MAX_ORDERS = 4;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        Menu.displayMenu();

        System.out.println("\n===============================================");
        System.out.println("              INFORMASI PEMESANAN");
        System.out.println("===============================================");
        System.out.println("* Maksimal 4 menu berbeda per transaksi");
        System.out.println("* Format input: NAMA MENU = JUMLAH");
        System.out.println("  Contoh: Nasi Ayam Goreng = 2");
        System.out.println("* Ketik 'selesai' untuk menyelesaikan pesanan");
        System.out.println("-----------------------------------------------");
        System.out.println("PROMO SPESIAL:");
        System.out.println("  Diskon 10% untuk pembelian > Rp 100.000");
        System.out.println("  Beli 1 Gratis 1 Minuman untuk pembelian > Rp 50.000");
        System.out.println("===============================================");
        System.out.println("\nSilakan masukkan pesanan Anda:");
        
        String[] orders = new String[MAX_ORDERS];
        collectionOrders(orders, 0);

        TotalDisc = 0;
        FreeDrinkQty = 0;
        
        double totalCost = calculateTotalCost(orders);
        printReceipt(orders, totalCost);

        scanner.close();
    }

    public static void collectionOrders(String[] orders, int index) {
        if (index >= MAX_ORDERS) {
            return;
        }

        String input = scanner.nextLine();
        if (!input.equalsIgnoreCase("selesai")) {
            orders[index] = input;
            collectionOrders(orders, index + 1);
        }
    }
}   