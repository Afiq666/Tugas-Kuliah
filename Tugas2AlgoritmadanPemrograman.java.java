import java.text.NumberFormat;
import java.util.Scanner;
import java.util.Locale;
import java.util.NumberFormat;

public class tugas2 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Locale indonesia = new Locale("id", "ID");
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(indonesia);

        String golongan;
        int jamLembur;
        double gajiPokok = 0;
        double gajiLembur = 0;

        while(true){
            System.out.println("Masukkan Golongan Karyawan (A/B/C) : ");
            golongan = input.next().toUpperCase();

            if(!golongan.equals("A") && !golongan.equals("B") && !golongan.equals("C")) {
                System.out.println("Golongan Tidak Valid! Harus A/B/C\n");
            } else {
                break;
            }
        }

        while(true){
            System.out.println("Masukkan Jam Lembur Karyawan : ");
            jamLembur = input.nextInt();

            if(jamLembur >=0) {
                break;
            } else {
                System.out.println("Jam Lembur Tidak Valid! Minimal 1 Jam\n");
            }
        }

        switch (golongan) {
            case "A":
                gajiPokok = 5000000;
                break;
            case "B":
                gajiPokok = 6500000;
                break;
            case "C":
                gajiPokok = 9500000;
                break;
            default:
                break;
        }

        double persenanLembur;
        if (jamLembur == 0) {
            persenanLembur = 0.0;
        } else if (jamLembur == 1) {
            persenanLembur = 0.30;
        } else if (jamLembur == 2) {
            persenanLembur = 0.32;
        } else if (jamLembur == 3) {
            persenanLembur = 0.34;
        } else if (jamLembur == 4) {
            persenanLembur = 0.36;
        } else {
            persenanLembur = 0.38;
        }

        gajiLembur = gajiPokok * persenanLembur;
        double totalPenghasilan = gajiPokok + gajiLembur;

        System.out.println("\n===== Total Penghasilan =====");
        System.out.println("Golongan          : " + golongan);
        System.out.println("Jam Lembur        : " + jamLembur + " Jam");
        System.out.println("Gaji Pokok        : " + rupiah.format(gajiPokok));
        System.out.println("Gaji Lembur       : " + rupiah.format(gajiLembur));
        System.out.println("Total Penghasilan : " + rupiah.format(totalPenghasilan));

        input.close();

    }
}