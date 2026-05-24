/**
 * tracking-app.js
 * Vue 3 — Halaman Tracking Delivery Order SITTA UT
 */

const { createApp } = Vue;

const app = createApp({

    data() {
        return {
            // Referensi data master dari dataBahanAjar.js
            upbjjList:    dataBahanAjar.upbjjList,
            pengirimanList: dataBahanAjar.pengirimanList,
            paketList:    dataBahanAjar.paket,
            trackingList: dataBahanAjar.tracking,

            // Objek penampung input form DO baru
            newDO: {
                nomorDO:      "",
                nim:          "",
                nama:         "",
                ekspedisi:    "",
                paket:        "",
                tanggalKirim: "",
                total:        0
            }
        };
    },

    computed: {
        /**
         * selectedPaket — Mengembalikan objek paket lengkap berdasarkan
         * kode yang dipilih di <select>. Dipakai untuk menampilkan
         * detail isi paket secara reaktif di bawah dropdown.
         */
        selectedPaket() {
            return this.paketList.find(p => p.kode === this.newDO.paket);
        },

        /**
         * nextNomorDO — Auto-generate nomor DO dengan format DO{tahun}-{seq}.
         * Urutan dihitung dari jumlah key yang sudah ada di trackingList,
         * lalu diformat 4 digit dengan padStart.
         * Contoh: DO2026-0002
         */
        nextNomorDO() {
            const tahun   = new Date().getFullYear();
            const jumlah  = Object.keys(this.trackingList).length;
            const urutan  = String(jumlah + 1).padStart(4, "0");
            return `DO${tahun}-${urutan}`;
        }
    },

    watch: {
        /**
         * Watcher 1 — selectedPaket:
         * Setiap kali computed selectedPaket berubah (pengguna mengganti
         * pilihan paket), total harga diperbarui secara otomatis tanpa
         * pengguna harus mengisi manual.
         */
        selectedPaket(paketBaru) {
            this.newDO.total = paketBaru ? paketBaru.harga : 0;
        },

        /**
         * Watcher 2 — newDO.ekspedisi:
         * Memantau setiap pergantian ekspedisi. Berguna sebagai hook
         * untuk logika tambahan seperti kalkulasi ongkir di masa depan.
         */
        "newDO.ekspedisi"(pilihanBaru) {
            if (pilihanBaru) {
                console.log(`[Tracking] Ekspedisi dipilih: ${pilihanBaru}`);
            }
        }
    },

    methods: {
        submitDO() {
            // Validasi: semua field wajib harus terisi sebelum data disimpan
            if (!this.newDO.nim || !this.newDO.nama || !this.newDO.ekspedisi || !this.newDO.paket) {
                Swal.fire({
                    icon:               "warning",
                    title:              "Data Belum Lengkap",
                    text:               "Pastikan NIM, Nama, Ekspedisi, dan Paket sudah terisi.",
                    confirmButtonColor: "#f39c12"
                });
                return;
            }

            // Ambil nomor DO yang sudah di-generate dan catat waktu pembuatan
            const nomor    = this.nextNomorDO;
            const tanggal  = this.newDO.tanggalKirim || new Date().toISOString().split("T")[0];
            const waktuNow = new Date().toLocaleString("id-ID");

            // Susun objek data DO baru beserta riwayat perjalanan awal
            const newData = {
                nim:          this.newDO.nim,
                nama:         this.newDO.nama,
                status:       "Dalam Proses",
                ekspedisi:    this.newDO.ekspedisi,
                tanggalKirim: tanggal,
                paket:        this.newDO.paket,
                total:        this.newDO.total,
                perjalanan: [
                    {
                        waktu:      waktuNow,
                        keterangan: "Data DO dibuat dan menunggu pengiriman"
                    }
                ]
            };

            // Daftarkan DO baru ke trackingList dengan nomor sebagai key
            this.trackingList[nomor] = newData;

            Swal.fire({
                icon:              "success",
                title:             "DO Berhasil Dibuat",
                html: `
                    <p>Nomor DO : <b>${nomor}</b></p>
                    <p>Nama     : <b>${this.newDO.nama}</b></p>
                    <p>Paket    : <b>${this.newDO.paket}</b></p>
                    <p>Total    : <b>Rp ${this.newDO.total.toLocaleString("id-ID")}</b></p>
                `,
                confirmButtonText:  "Oke",
                confirmButtonColor: "#3085d6"
            });

            // Reset form; nomor DO otomatis maju ke urutan berikutnya
            this.newDO = {
                nomorDO:      this.nextNomorDO,
                nim:          "",
                nama:         "",
                ekspedisi:    "",
                paket:        "",
                tanggalKirim: "",
                total:        0
            };
        }
    },

    // Inisialisasi nomor DO pertama kali saat halaman selesai dimuat
    mounted() {
        this.newDO.nomorDO = this.nextNomorDO;
    }
});

app.mount("#trackingApp");
