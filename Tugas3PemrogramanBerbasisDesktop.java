import java.util.*;
import java.io.*;

abstract class MenuItem {
    private String nama;
    private double harga;

    public MenuItem(String nama, double harga) {
        this.nama = nama;
        this.harga = harga;
    }

    public String getNama() {return nama; }
    public double getHarga() {return harga; }

    public abstract void tampilMenu();
    public abstract String toFile();
}

class Makanan extends MenuItem {
    private String jenis;

    public Makanan(String nama, double harga, String jenis) {
        super(nama, harga);
        this.jenis = jenis;
    }

    @Override
    public void tampilMenu() {
        System.out.println(getNama() + " (" + jenis + ") - Rp " + (int)getHarga());
    }

    @Override
    public String toFile() {
        return "MAKANAN," + getNama() + "," + getHarga() + "," +jenis;
    }
}

class Minuman extends MenuItem {
    private String jenis;

    public Minuman(String nama, double harga, String jenis) {
        super(nama, harga);
        this.jenis = jenis;
    }

    @Override
    public void tampilMenu() {
        System.out.println(getNama() + " (" + jenis + ") - Rp " + (int)getHarga());
    }

    @Override
    public String toFile() {
        return "MINUMAN," + getNama() + "," + getHarga() + "," + jenis;
    }
}

class Diskon extends MenuItem {
    private double persen;

    public Diskon(String nama, double persen) {
        super(nama, 0);
        this.persen = persen;
    }

    public double getPersen() { return persen; }

    @Override
    public void tampilMenu() {
        System.out.println(getNama() + " - Diskon " + (int)persen + "%");
    }

    @Override
    public String toFile() {
        return "DISKON," + getNama() + "," + persen;
    }
}

class Menu {
    private ArrayList<MenuItem> items = new ArrayList<>();

    public void tambah(MenuItem item) {
        items.add(item);
    }

    public void tampilkan() {
        System.out.println(" ===== MAKANAN ===== ");
        for (MenuItem item : items) {
            if (item instanceof Makanan) item.tampilMenu();
        }
        System.out.println(" ===== MINUMAN ===== ");
        for (MenuItem item : items) {
            if (item instanceof Minuman) item.tampilMenu();
        }
        System.out.println(" ===== DISKON ===== ");
        for (MenuItem item : items) {
            if (item instanceof  Diskon) item.tampilMenu();
        }
    }

    public MenuItem cari(String nama) throws Exception {
        for (MenuItem item : items) {
            if (item.getNama().equalsIgnoreCase(nama)) return item;
        }
        throw new Exception("Menu '" + nama + "' tidak ada!");
    }

    public void simpan(String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (MenuItem item : items) writer.println(item.toFile());
            System.out.println("Menu Berhasil Disimpan!");
        } catch (IOException e) {
            System.out.println("Kesalahan Dalam Menyimpan : " + e.getMessage());
        }
    }

    public void muat(String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            items.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals("MAKANAN")) {
                    items.add(new Makanan(data[1], Double.parseDouble(data[2]), data[3]));
                } else if (data[0].equals("MINUMAN")) {
                    items.add(new Minuman(data[1], Double.parseDouble(data[2]), data[3]));
                } else if (data[0].equals("DISKON")) {
                    items.add(new Diskon(data[1], Double.parseDouble(data[2])));
                }
            }
            System.out.println("Menu Berhasil Dimuat!");
        } catch (Exception e) {
            System.out.println("File tidak ditemukan, silakan gunakan menu default");
        }
    }
}

class Pesanan {
    private ArrayList<MenuItem> items = new ArrayList<>();
    private ArrayList<Integer> qty = new ArrayList<>();

    public void tambah(MenuItem item, int jumlah) {
        items.add(item);
        qty.add(jumlah);
    }

    public void struk(String file) {
        double subtotal = 0;

        System.out.println(" ===== STRUK PEMBAYARAN ===== ");

        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);
            if (!(item instanceof Diskon)) {
                double total = item.getHarga() * qty.get(i);
                System.out.println(item.getNama() + "x" + qty.get(i) + " = Rp " + (int)total);
                subtotal += total;
            }
        }

        double diskon = 0;
        for (MenuItem item : items) {
            if (item instanceof Diskon) {
                diskon += subtotal * (((Diskon)item).getPersen() / 100);
                System.out.println("Diskon" + item.getNama() + " (" + (int)((Diskon)item).getPersen() + "%)");
            }
        }

        double pajak = (subtotal - diskon) * 0.1;
        double total = subtotal - diskon + pajak + 20000;

        System.out.println("---------------------------");
        System.out.println("Subtotal : Rp " + (int)subtotal);
        if (diskon > 0) System.out.println("Diskon : - Rp " + (int)diskon);
        System.out.println("Pajak (10%) : Rp " + (int)pajak);
        System.out.println("Layanan : Rp 20000");
        System.out.println("TOTAL : Rp " + (int)total);
        System.out.println("===========================");

        try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
            writer.println(" ===== STRUK PEMBAYARAN " + new Date() + " ===== ");
            for (int i = 0; i < items.size(); i++) {
                if (!(items.get(i) instanceof Diskon)) {
                    writer.println(items.get(i).getNama() + "x" + qty.get(i));
                }
            }
            writer.println("Subtotal : Rp " + (int)subtotal);
            if (diskon > 0) writer.println("Diskon : - Rp " + (int)diskon);
            writer.println("TOTAL : Rp " + (int)total);
            System.out.println("Struk berhasil disimpan!");
        } catch (IOException e) {
            System.out.println("Kesalahan saat menyimpan struk");
        }
    }
}

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static Menu menu = new Menu();

    public static void main (String[] args) {
        menu.tambah(new Makanan("Nasi Goreng", 15000, "Nasi"));
        menu.tambah(new Makanan("Mie Goreng", 12000, "Mie"));
        menu.tambah(new Minuman("Es Teh", 5000, "Dingin"));
        menu.tambah(new Minuman("Kopi", 8000, "Panas"));
        menu.tambah(new Diskon("Member", 10));

        menu.muat("menu.txt");

        while (true) {
            System.out.println(" ===== RESTORAN KAMI ===== ");
            System.out.println(" 1. Tambah Menu");
            System.out.println(" 2. Lihat Menu");
            System.out.println(" 3. Pesan");
            System.out.println(" 4. Simpan Menu Baru");
            System.out.println(" 5. Keluar");
            System.out.println(" Pilih : ");

            try {
                String pilihan = scanner.nextLine();
                if (pilihan.equals("1")) tambahMenu();
                else if (pilihan.equals("2")) menu.tampilkan();
                else if (pilihan.equals("3")) pesan();
                else if (pilihan.equals("4")) menu.simpan("menu.txt");
                else if (pilihan.equals("5")) break;
            } catch (Exception e) {
                System.out.println("Terjadi Kesalahan Dalam Input : " + e.getMessage());
            }
        }
    }

    static void tambahMenu() {
        System.out.println(" Jenis (1:Makanan 2:Minuman 3:Diskon) :");
        String jenis = scanner.nextLine();

        try {
            System.out.println(" Nama : ");
            String nama = scanner.nextLine();

            if (jenis.equals("1") || jenis.equals("2")) {
                System.out.println(" Harga :");
                double harga = Double.parseDouble(scanner.nextLine());
                System.out.println(" Jenis : ");
                String jenisItem = scanner.nextLine();
                if (jenis.equals("1")) menu.tambah(new Makanan(nama, harga, jenisItem));
                else menu.tambah(new Minuman(nama, harga, jenisItem));
            } else {
                System.out.println(" Persen : ");
                double persen = Double.parseDouble(scanner.nextLine());
            }
            System.out.println(" Berhasil Ditambahkan!");
        } catch (Exception e) {
            System.out.println(" Kesalahan Input");
        }
    }

    static void pesan() {
        menu.tampilkan();
        Pesanan pesanan = new Pesanan();

        System.out.println(" ===== PESANAN =====");
        System.out.println(" Ketik 'selesai' untuk menyelesaikan pesanan");

        while (true) { 
            System.out.println(" Menu : ");
            String nama = scanner.nextLine();
            if (nama.equalsIgnoreCase("selesai")) break;

            try {
                MenuItem item = menu.cari(nama);
                if (item instanceof Diskon) {
                    pesanan.tambah(item, 1);
                } else {
                    System.out.println(" Jumlah : ");
                    int jumlah = Integer.parseInt(scanner.nextLine());
                    pesanan.tambah(item, jumlah);
                }
                System.out.println(" OK!");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        pesanan.struk("struk.txt");
    }
}