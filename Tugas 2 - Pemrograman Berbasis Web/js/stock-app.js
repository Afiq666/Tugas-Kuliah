/**
 * stock-app.js
 * Vue 3 — Halaman Stok Bahan Ajar SITTA UT
 */

const { createApp } = Vue;

const app = createApp({

    data() {
        return {
            // Referensi data master dari dataBahanAjar.js
            upbjjList:    dataBahanAjar.upbjjList,
            kategoriList: dataBahanAjar.kategoriList,
            stok:         dataBahanAjar.stok,

            // State reaktif: filter, pengurutan, dan kondisi checkbox
            selectedUpbjj:    "",
            selectedKategori: "",
            isSafety:         false,
            sortBy:           "",

            // Objek penampung nilai form tambah data baru
            newData: {
                kode:        "",
                judul:       "",
                kategori:    "",
                upbjj:       "",
                lokasiRak:   "",
                harga:       0,
                stok:        0,
                catatanHTML: ""
            }
        };
    },

    computed: {
        /**
         * kategoriListFiltered — Dependent dropdown:
         * Mengumpulkan kategori unik yang hanya tersedia di UPBJJ yang sedang dipilih.
         * Mengembalikan array kosong selama UPBJJ belum dipilih, sehingga
         * dropdown kategori tetap non-aktif (disabled di HTML).
         */
        kategoriListFiltered() {
            if (!this.selectedUpbjj) return [];
            const matching = this.stok
                .filter(item => item.upbjj === this.selectedUpbjj)
                .map(item => item.kategori);
            return [...new Set(matching)];
        },

        /**
         * filteredData — Computed utama filter + sort.
         * Vue men-cache hasil ini; penghitungan ulang hanya terjadi ketika
         * salah satu dependency (selectedUpbjj, selectedKategori, isSafety, sortBy)
         * berubah — lebih efisien dibanding method biasa yang selalu re-run.
         */
        filteredData() {
            // Salin array agar data sumber tidak termutasi langsung
            let result = [...this.stok];

            // Saring berdasarkan UPBJJ yang dipilih
            if (this.selectedUpbjj)
                result = result.filter(i => i.upbjj === this.selectedUpbjj);

            // Saring berdasarkan kategori (hanya relevan jika UPBJJ sudah dipilih)
            if (this.selectedKategori)
                result = result.filter(i => i.kategori === this.selectedKategori);

            // Tampilkan hanya stok yang qty-nya di bawah safety atau sudah habis
            if (this.isSafety)
                result = result.filter(i => i.qty <= i.safety || i.qty === 0);

            // Terapkan pengurutan sesuai pilihan pengguna
            if (this.sortBy === "judul")
                result.sort((a, b) => a.judul.localeCompare(b.judul));
            else if (this.sortBy === "qty")
                result.sort((a, b) => a.qty - b.qty);
            else if (this.sortBy === "harga")
                result.sort((a, b) => a.harga - b.harga);

            return result;
        }
    },

    watch: {
        /**
         * Watcher 1 — selectedUpbjj:
         * Setiap kali pilihan UPBJJ berubah, reset kategori yang sebelumnya
         * terpilih agar tidak muncul nilai yang sudah tidak relevan.
         */
        selectedUpbjj() {
            this.selectedKategori = "";
        },

        /**
         * Watcher 2 — isSafety:
         * Saat filter stok kritis diaktifkan, reset sort ke default
         * supaya tampilan data yang sudah tersaring tidak membingungkan.
         */
        isSafety(aktif) {
            if (aktif) this.sortBy = "";
        }
    },

    methods: {
        // Kembalikan semua filter dan sort ke kondisi awal
        resetFilter() {
            this.selectedUpbjj    = "";
            this.selectedKategori = "";
            this.isSafety         = false;
            this.sortBy           = "";
        },

        // Tentukan teks label status berdasarkan perbandingan qty dan safety
        statusText(item) {
            if (item.qty === 0)           return "Kosong";
            if (item.qty < item.safety)   return "Menipis";
            return "Aman";
        },

        // Tentukan nama class CSS untuk pewarnaan baris status
        statusKelas(item) {
            if (item.qty === 0)           return "danger";
            if (item.qty < item.safety)   return "warning";
            return "success";
        },

        // ── TAMBAH DATA ────────────────────────────────────────────────
        async openAddModal() {
            // Bangun opsi dropdown UPBJJ dan Kategori dari data reaktif
            const upbjjOptions = this.upbjjList
                .map(u => `<option value="${u}">${u}</option>`)
                .join("");

            const kategoriOptions = this.kategoriList
                .map(k => `<option value="${k}">${k}</option>`)
                .join("");

            const { value: formValues } = await Swal.fire({
                title: "Tambah Bahan Ajar",
                width: 600,
                html: `
                    <input id="kode"        class="swal2-input"    placeholder="Kode MK">
                    <input id="judul"       class="swal2-input"    placeholder="Judul">
                    <select id="kategori"   class="swal2-select">
                        <option value="">Pilih Kategori</option>${kategoriOptions}
                    </select>
                    <select id="upbjj"      class="swal2-select">
                        <option value="">Pilih UPBJJ</option>${upbjjOptions}
                    </select>
                    <input id="lokasiRak"   class="swal2-input"    placeholder="Lokasi Rak">
                    <input id="harga"       class="swal2-input"    type="number" placeholder="Harga">
                    <input id="qty"         class="swal2-input"    type="number" placeholder="Jumlah Stok">
                    <textarea id="catatanHTML" class="swal2-textarea" placeholder="Catatan (boleh HTML)"></textarea>
                `,
                focusConfirm: false,
                showCancelButton:  true,
                confirmButtonText: "Simpan",
                cancelButtonText:  "Batal",
                preConfirm: () => ({
                    kode:        document.getElementById("kode").value.trim(),
                    judul:       document.getElementById("judul").value.trim(),
                    kategori:    document.getElementById("kategori").value,
                    upbjj:       document.getElementById("upbjj").value,
                    lokasiRak:   document.getElementById("lokasiRak").value.trim(),
                    harga:       Number(document.getElementById("harga").value),
                    qty:         Number(document.getElementById("qty").value),
                    catatanHTML: document.getElementById("catatanHTML").value.trim(),
                    safety:      10
                })
            });

            if (!formValues) return;

            // Validasi: kode dan judul wajib diisi
            if (!formValues.kode || !formValues.judul) {
                Swal.fire("Validasi Gagal", "Kode dan Judul wajib diisi!", "error");
                return;
            }

            // Tolak jika kode sudah terdaftar di stok
            if (this.stok.some(i => i.kode === formValues.kode)) {
                Swal.fire("Duplikat", "Kode tersebut sudah terdaftar!", "error");
                return;
            }

            this.stok.push(formValues);
            Swal.fire("Berhasil", "Data bahan ajar berhasil ditambahkan!", "success");
        },

        // ── EDIT DATA ──────────────────────────────────────────────────
        async editItem(item) {
            const upbjjOptions = this.upbjjList
                .map(u => `<option value="${u}" ${u === item.upbjj ? "selected" : ""}>${u}</option>`)
                .join("");

            const kategoriOptions = this.kategoriList
                .map(k => `<option value="${k}" ${k === item.kategori ? "selected" : ""}>${k}</option>`)
                .join("");

            const { value: formValues } = await Swal.fire({
                title: `Edit: ${item.kode}`,
                width: 600,
                html: `
                    <input id="judul"       class="swal2-input"  value="${item.judul}">
                    <select id="kategori"   class="swal2-select">${kategoriOptions}</select>
                    <select id="upbjj"      class="swal2-select">${upbjjOptions}</select>
                    <input id="lokasiRak"   class="swal2-input"  value="${item.lokasiRak}">
                    <input id="harga"       class="swal2-input"  type="number" value="${item.harga}">
                    <input id="qty"         class="swal2-input"  type="number" value="${item.qty}">
                    <textarea id="catatanHTML" class="swal2-textarea">${item.catatanHTML || ""}</textarea>
                `,
                focusConfirm: false,
                showCancelButton:  true,
                confirmButtonText: "Simpan Perubahan",
                cancelButtonText:  "Batal",
                preConfirm: () => ({
                    judul:       document.getElementById("judul").value.trim(),
                    kategori:    document.getElementById("kategori").value,
                    upbjj:       document.getElementById("upbjj").value,
                    lokasiRak:   document.getElementById("lokasiRak").value.trim(),
                    harga:       Number(document.getElementById("harga").value),
                    qty:         Number(document.getElementById("qty").value),
                    catatanHTML: document.getElementById("catatanHTML").value.trim()
                })
            });

            if (formValues) {
                Object.assign(item, formValues);
                Swal.fire("Berhasil", "Data bahan ajar berhasil diperbarui!", "success");
            }
        },

        // ── HAPUS DATA ─────────────────────────────────────────────────
        deleteItem(item) {
            Swal.fire({
                title:             "Hapus data ini?",
                text:              `"${item.judul}" akan dihapus secara permanen.`,
                icon:              "warning",
                showCancelButton:  true,
                confirmButtonText: "Ya, hapus!",
                cancelButtonText:  "Batal",
                confirmButtonColor: "#e53935"
            }).then(result => {
                if (result.isConfirmed) {
                    this.stok = this.stok.filter(i => i.kode !== item.kode);
                    Swal.fire("Terhapus!", "Data telah dihapus.", "success");
                }
            });
        }
    }
});

app.mount("#stockApp");
