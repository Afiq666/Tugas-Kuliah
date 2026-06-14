import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

public class tugas3 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Locale indonesia = new Locale("id", "ID");
        NumberFormat rupiah = NumberFormat.getCurrencyInstance(indonesia);

        double[] arrayGaji = {5000000, 6500000, 9500000};

        int[] arrayPersenLembur = {30, 32, 34, 36, 38};

        String golongan;
        int jamLembur;
        int indexGaji = 0;

        while(true) {
            System.out.println("Masukkan Golongan Karyawan (A/B/C) : ");
            golongan = input.next().toUpperCase();

            if (golongan.equals("A")) {
                indexGaji = 0;
                break;
            } else if (golongan.equals("B")) {
                indexGaji = 1;
                break;
            } else if (golongan.equals("C")) {
                indexGaji = 2;
                break;
            } else {
                System.out.println("Golongan Tidak Valid! Harus A/B/C\n");
            }
        }

        while (true) {
            System.out.println("Masukkan Jam Lembur Karyawan : ");
            jamLembur = input.nextInt();

            if(jamLembur >= 0) {
                break;
            } else {
                System.out.println("Jam Lembur Tidak Valid! Minimal 1 Jam\n ");
            }
        }

        double gajiPokok = arrayGaji[indexGaji];
        double gajiLembur = 0;

        if (jamLembur > 0) {
            int indexLembur;

            if (jamLembur == 1) {
                indexLembur = 0;
            } else if (jamLembur == 2) {
                indexLembur = 1;
            } else if (jamLembur == 3) {
                indexLembur = 2;
            } else if (jamLembur == 4) {
                indexLembur = 3;
            } else {
                indexLembur = 4;
            }

            gajiLembur = gajiPokok * (arrayPersenLembur[indexLembur] / 100.0);
        }

        double totalPenghasilan = gajiPokok + gajiLembur;

        System.out.println("\n===== Total Penghasilan =====");
        System.out.println("Golongan   : " + golongan);
        System.out.println("Jam Lembur : " + jamLembur + " Jam");
        System.out.println("Gaji Pokok : " + rupiah.format(gajiPokok));

        if (jamLembur > 0) {
            int persenDigunakan = (jamLembur >=5 ? arrayPersenLembur[4] : arrayPersenLembur[jamLembur - 1]);
            System.out.println("Gaji Lembur ("+ persenDigunakan +"%) : " + rupiah.format(gajiLembur));
        } else {
            System.out.println("Gaji Lembur : " + rupiah.format(gajiLembur));
        }

        System.out.println("Total Penghasilan : " + rupiah.format(totalPenghasilan));

        input.close();
    }
}