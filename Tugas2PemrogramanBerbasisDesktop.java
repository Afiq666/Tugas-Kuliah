import java.util.Scanner;

class Menu {
    String nama;
    double harga;
    String kategori;

    Menu(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }
}

public class Main {
    static Menu[] menuList = new Menu[50];
    static int jumlahMenu = 0;
    static String[] pesanan = new String[100];
    static int jumlahPesanan = 0;
    static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {
        isiMenuAwal();

        while (true) {
            System.out.println("===== APLIKASI SEDERHANA =====");
            System.out.println("1. Menu Pelanggan");
            System.out.println("2. Menu Pemilik");
            System.out.println("3. Keluar");
            System.out.println("Pilih : ");
            String pilih = input.nextLine();

            if (pilih.equals("1")) {
                menuPelanggan();
            } else if (pilih.equals("2")) {
                menuPemilik();
            } else if (pilih.equals("3")) {
                break;
            } else {
                System.out.println("Pilihan anda tidak valid");
            }
        }
    }

    static void isiMenuAwal() {
        menuList[jumlahMenu++] = new Menu("Nasi Goreng", 15000, "makanan");
        menuList[jumlahMenu++] = new Menu("Mie Goreng", 12000, "makanan");
        menuList[jumlahMenu++] = new Menu("Ayam Bakar", 20000, "makanan");
        menuList[jumlahMenu++] = new Menu("Soto Sapi", 13000, "makanan");
        menuList[jumlahMenu++] = new Menu("Kopi", 8000, "minuman");
        menuList[jumlahMenu++] = new Menu("Es Teh", 5000, "minuman");
        menuList[jumlahMenu++] = new Menu("Es Jeruk", 6000, "minuman");
        menuList[jumlahMenu++] = new Menu("Jus Mangga", 10000, "minuman");
    }

    static void tampilMenu() {
        System.out.println("===== MENU MAKANAN =====");
        for (int i = 0; i < jumlahMenu; i++) {
            if (menuList[i].kategori.equals("makanan")) {
                System.out.println(menuList[i].nama + " - Rp " +
                        (int) menuList[i].harga);
            }
        }

        System.out.println("===== MENU MINUMAN =====");
        for (int i = 0; i < jumlahMenu; i++) {
            if (menuList[i].kategori.equals("minuman")) {
                System.out.println(menuList[i].nama + " - Rp " +
                        (int) menuList[i].harga);
            }
        }
    }

    static void menuPelanggan() {
        tampilMenu();
        System.out.println("===== PEMESANAN =====");
        System.out.println("Format Pemesanan : Nama Menu = Jumlah");
        System.out.println("Ketik 'selesai' jika sudah selesai memesan");
        System.out.println("Diskon 10% Jika Belanja Diatas 100rb");
        System.out.println("Diskon Beli 1 Gratis 1 Minuman Jika Belanja Diatas 50rb");

        jumlahPesanan = 0;

        while (true) {
            System.out.println("Pesanan : ");
            String pesan = input.nextLine();

            if (pesan.equalsIgnoreCase("selesai")) {
                break;
            }

            if (!pesan.contains("=")) {
                System.out.println("Format Yang Dimasukkan Salah!");
                continue;
            }

            String[] split = pesan.split("=");
            String namaMenu = split[0].trim();

            boolean ada = false;
            for (int i = 0; i < jumlahMenu; i++) {
                if (menuList[i].nama.equalsIgnoreCase(namaMenu)) {
                    ada = true;
                    break;
                }
            }

            if (!ada) {
                System.out.println("Menu Yang Anda Masukkan Tidak Ditemukan!");
                continue;
            }

            pesanan[jumlahPesanan++] = pesan;
            System.out.println("Pesanan Telah Ditambahkan!");
        }

        if (jumlahPesanan > 0) {
            hitungTotal();
        }
    }

    static void hitungTotal() {
        double subtotal = 0;
        int totalMinuman = 0;

        for (int i = 0; i < jumlahPesanan; i++) {
            String[] split = pesanan[i].split("=");
            String namaMenu = split[0].trim();
            int qty = Integer.parseInt(split[1].trim());

            for (int j = 0; j < jumlahMenu; j++) {
                if (menuList[j].nama.equalsIgnoreCase(namaMenu)) {
                    subtotal += menuList[j].harga * qty;
                    if (menuList[j].kategori.equals("minuman")) {
                        totalMinuman += qty;
                    }
                    break;
                }
            }
        }

        System.out.println("===== DETAIL PEMESANAN =====");
        System.out.println("Subtotal : Rp " + (int) subtotal);

        double diskon = 0;
        if (subtotal > 100000) {
            diskon = subtotal * 0.1;
            System.out.println("Diskon 10% : Rp " + (int) diskon);
        }

        int gratisMinuman = 0;
        if (subtotal >= 50000 && totalMinuman > 0) {
            gratisMinuman = totalMinuman / 2;
            System.out.println("Gratis " + gratisMinuman + " minuman!");
        }

        double setelahDiskon = subtotal - diskon;
        double pajak = setelahDiskon * 0.1;
        double total = setelahDiskon + pajak + 20000;

        System.out.println("Pajak 10% : " + (int) pajak);
        System.out.println("Biaya Layanan : Rp 20000");

        cetakStruk(total);
    }

    static void cetakStruk(double total) {
        System.out.println("===== STRUK PEMBAYARAN =====");

        for (int i = 0; i < jumlahPesanan; i++) {
            String[] split = pesanan[i].split("=");
            String namaMenu = split[0].trim();
            int qty = Integer.parseInt(split[1].trim());

            for (int j = 0; j < jumlahMenu; j++) {
                if (menuList[j].nama.equalsIgnoreCase(namaMenu)) {
                    double hargaItem = menuList[j].harga * qty;
                    System.out.println("@Rp " + (int) menuList[j].harga + " = Rp " + (int) hargaItem);
                    break;
                }
            }
        }
        System.out.println("TOTAL MEMBAYAR : Rp " + (int) total);
        System.out.println("===================================");
    }

    static void menuPemilik() {
        while (true) {
            System.out.println("===== MENU PEMILIK =====");
            System.out.println("1. Melihat Menu");
            System.out.println("2. Menambahkan Menu");
            System.out.println("3. Mengubah Harga Menu");
            System.out.println("4. Menghapus Menu");
            System.out.println("5. Kembali");
            System.out.println("Pilih : ");
            String pilih = input.nextLine();

            if (pilih.equals("1")) {
                tampilMenu();
            } else if (pilih.equals("2")) {
                tambahMenu();
            } else if (pilih.equals("3")) {
                ubahHarga();
            } else if (pilih.equals("4")) {
                hapusMenu();
            } else if (pilih.equals("5")) {
                break;
            } else {
                System.out.println("Pilihan Anda Tidak Valid!");
            }
        }
    }

    static void tambahMenu() {
        System.out.println("Jumlah Menu Yang Akan Ditambahkan : ");
        int banyak = Integer.parseInt(input.nextLine());

        for (int i = 0; i < banyak; i++) {
            System.out.println("Menu ke-" + (i + 1));
            System.out.println("Nama : ");
            String nama = input.nextLine();
            System.out.println("Harga : ");
            double harga = Double.parseDouble(input.nextLine());
            System.out.println("Kategori (makanan/minuman) : ");
            String kategori = input.nextLine();

            menuList[jumlahMenu++] = new Menu(nama, harga, kategori);
            System.out.println("Menu Baru Telah Berhasil Ditambahkan!");
        }
    }

    static void ubahHarga() {
        System.out.println("===== DAFTAR MENU =====");
        for (int i = 0; i < jumlahMenu; i++) {
            System.out.println((i + 1) + ". " + menuList[i].nama + " - Rp " + (int) menuList[i].harga);
        }

        System.out.println("Pilih Nomor Menu : ");
        int nomor = Integer.parseInt(input.nextLine()) - 1;

        System.out.println("Menu : " + menuList[nomor].nama);
        System.out.println("Harga Baru : ");
        double hargaBaru = Double.parseDouble(input.nextLine());

        System.out.println("Apakah Anda Yakin Ingin Mengubah Harga? (ya/tidak) : ");
        String yakin = input.nextLine();

        if (yakin.equalsIgnoreCase("ya")) {
            menuList[nomor].harga = hargaBaru;
            System.out.println("Harga Berhasil Diubah!");
        } else {
            System.out.println("Dibatalkan!");
        }
    }

    static void hapusMenu() {
        System.out.println("===== DAFTAR MENU =====");
        for (int i = 0; i < jumlahMenu; i++) {
            System.out.println((i + 1) + ". " + menuList[i].nama);
        }

        System.out.println("Pilih Nomor Menu : ");
        int nomor = Integer.parseInt(input.nextLine()) - 1;

        System.out.println("Apakah Anda Yakin Ingin Menghapus Menu? (yaa/tidak) : ");
        String yakin = input.nextLine();

        if (yakin.equalsIgnoreCase("ya")) {
            for (int i = nomor; i < jumlahMenu - 1; i++) {
                menuList[i] = menuList[i + 1];
            }
            jumlahMenu--;
            System.out.println("Menu Berhasil Dihapus!");
        } else {
            System.out.println("Dibatalkan!");
        }
    }
}