import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Interface untuk manajemen stok
interface ManajemenStok {
    void tambahStok(int jumlah); // Menambahkan stok obat
    void kurangiStok(int jumlah) throws Exception; // Mengurangi stok obat dengan validasi
}

// Superclass Obat
class Obat {
    protected String idObat; // ID obat
    protected String namaObat; // Nama obat
    protected int stok; // Stok obat
    protected Date tanggalKadaluarsa; // Tanggal kadaluarsa obat

    // Konstruktor untuk inisialisasi objek Obat
    public Obat(String idObat, String namaObat, int stok, Date tanggalKadaluarsa) {
        this.idObat = idObat;
        this.namaObat = namaObat;
        this.stok = stok;
        this.tanggalKadaluarsa = tanggalKadaluarsa;
    }

    // Getter untuk ID obat
    public String getIdObat() {
        return idObat;
    }

    // Getter untuk nama obat
    public String getNamaObat() {
        return namaObat;
    }

    // Getter untuk stok obat
    public int getStok() {
        return stok;
    }

    // Getter untuk tanggal kadaluarsa dengan format yang ditentukan
    public String getTanggalKadaluarsa() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(tanggalKadaluarsa);
    }

    @Override
    public String toString() {
        // Menyusun informasi obat dalam bentuk string
        return "ID Obat: " + idObat + 
               ", Nama Obat: " + namaObat + 
               ", Stok: " + stok + 
               ", Tanggal Kadaluarsa: " + tanggalKadaluarsa;
    }
}

// Subclass ObatResep yang merupakan turunan dari Obat
class ObatResep extends Obat {
    private String resepDokter; // Informasi resep dokter

    // Konstruktor untuk inisialisasi ObatResep
    public ObatResep(String idObat, String namaObat, int stok, Date tanggalKadaluarsa, String resepDokter) {
        super(idObat, namaObat, stok, tanggalKadaluarsa);
        this.resepDokter = resepDokter;
    }

    // Getter untuk resep dokter
    public String getResepDokter() {
        return resepDokter;
    }
}

// Implementasi Interface untuk manajemen stok
class StokObat implements ManajemenStok {
    private Obat obat; // Objek obat yang dikelola

    // Konstruktor untuk inisialisasi StokObat
    public StokObat(Obat obat) {
        this.obat = obat;
    }

    @Override
    public void tambahStok(int jumlah) {
        obat.stok += jumlah; // Menambahkan jumlah stok
        System.out.println("Stok berhasil ditambahkan. Stok saat ini: " + obat.stok);
    }

    @Override
    public void kurangiStok(int jumlah) throws Exception {
        if (obat.stok < jumlah) {
            // Validasi jika stok tidak mencukupi
            throw new Exception("Stok tidak mencukupi!");
        }
        obat.stok -= jumlah; // Mengurangi stok jika validasi terpenuhi
        System.out.println("Stok berhasil dikurangi. Stok saat ini: " + obat.stok);
    }
}

// Kelas untuk mengelola koneksi ke database
class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/apotek"; // URL database
    private static final String USER = "root"; // Username database
    private static final String PASSWORD = ""; // Password database

    // Method untuk mendapatkan koneksi database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

// Kelas utama untuk manajemen apotek
public class ManajemenApotek {
    private static ArrayList<Obat> daftarObat = new ArrayList<>(); // Daftar obat dalam memori

    public static void main(String[] args) throws NumberFormatException {
        Scanner scanner = new Scanner(System.in);
        boolean loginBerhasil = false;

        // Halaman Login
        while (!loginBerhasil) {
            try {
                System.out.println("\n+-----------------------------------------------------+");
                System.out.print("Username : ");
                String username = scanner.nextLine();
                if (username.trim().isEmpty()) {
                    throw new IllegalArgumentException("Username tidak boleh kosong.");
                }

                System.out.print("Password : ");
                String password = scanner.nextLine();
                if (password.trim().isEmpty()) {
                    throw new IllegalArgumentException("Password tidak boleh kosong.");
                }

                // Generate Captcha
                Random random = new Random();
                int captcha = random.nextInt(9000) + 1000; // Random angka 4 digit
                System.out.println("Captcha    : " + captcha);
                System.out.print("Masukkan Captcha: ");
                String captchaInputStr = scanner.nextLine();

                if (captchaInputStr.trim().isEmpty()) {
                    throw new IllegalArgumentException("Captcha tidak boleh kosong.");
                }
                int captchaInput = Integer.parseInt(captchaInputStr);

                // Validasi Login
                if (username.equals("admin") && password.equals("admin123") && captchaInput == captcha) {
                    loginBerhasil = true;
                    System.out.println("+-----------------------------------------------------+");
                    System.out.println("Login berhasil.");
                    System.out.println("+-----------------------------------------------------+");

                    // Tampilkan Selamat Datang dan Tanggal/Waktu
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println("Selamat Datang");
                    System.out.println("Tanggal dan Waktu : " + formatter.format(now));
                    System.out.println("+-----------------------------------------------------+");
                } else {
                    System.out.println("Login gagal. Username, password, atau captcha salah. Silakan coba lagi.");
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Kesalahan: " + e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Kesalahan: Input tidak valid.");
                scanner.nextLine(); // Membersihkan buffer
            }
        }

        // Menu Utama
        while (true) {
            System.out.println("\n=== Manajemen Stok Obat ===");
            System.out.println("1. Tambah Obat Baru");
            System.out.println("2. Lihat Daftar Obat");
            System.out.println("3. Tambah Stok Obat");
            System.out.println("4. Kurangi Stok Obat");
            System.out.println("5. Update Data Obat");
            System.out.println("6. Delete Data Obat");
            System.out.println("7. Keluar");
            System.out.print("Pilih menu: ");
            int pilihan = scanner.nextInt();

            switch (pilihan) {
                case 1:
                    // Tambah obat baru
                    scanner.nextLine(); // Membersihkan buffer
                    System.out.print("Masukkan ID Obat: ");
                    String id = scanner.nextLine();
                    System.out.print("Masukkan Nama Obat: ");
                    String nama = scanner.nextLine();
                    System.out.print("Masukkan Stok Awal: ");
                    int stok = scanner.nextInt();
                    scanner.nextLine(); // Membersihkan buffer
                    System.out.print("Masukkan Tanggal Kadaluarsa (yyyy-MM-dd): ");
                    String tanggalStr = scanner.nextLine();

                    try (Connection conn = DatabaseConnection.getConnection()) {
                        Date tanggal = new SimpleDateFormat("yyyy-MM-dd").parse(tanggalStr);
                        String query = "INSERT INTO obat (id_obat, nama_obat, stok, tanggal_kadaluarsa) VALUES (?, ?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(query);
                        stmt.setString(1, id);
                        stmt.setString(2, nama);
                        stmt.setInt(3, stok);
                        stmt.setString(4, new SimpleDateFormat("yyyy-MM-dd").format(tanggal));
                        stmt.executeUpdate();
                        System.out.println("Obat berhasil ditambahkan ke database.");
                    } catch (ParseException e) {
                        System.out.println("Format tanggal salah!");
                    } catch (SQLException e) {
                        System.out.println("Gagal menambahkan obat: " + e.getMessage());
                    }
                    break;

                case 2:
                    // Lihat daftar obat
                    daftarObat.clear(); // Kosongkan ArrayList sebelum mengambil data dari database
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String query = "SELECT * FROM obat";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        System.out.println("\nDaftar Obat:");
                        while (rs.next()) {
                            String idObat = rs.getString("id_obat");
                            String namaObat = rs.getString("nama_obat");
                            int jumlahStok = rs.getInt("stok");
                            Date tanggalKadaluarsa = rs.getDate("tanggal_kadaluarsa");

                            // Tambahkan data ke ArrayList
                            Obat obat = new Obat(idObat, namaObat, jumlahStok, tanggalKadaluarsa);
                            daftarObat.add(obat);
                        }

                        // Tampilkan data dari ArrayList
                        if (daftarObat.isEmpty()) {
                            System.out.println("Belum ada data obat.");
                        } else {
                            for (Obat obat : daftarObat) {
                                System.out.println(obat);
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Gagal mengambil data: " + e.getMessage());
                    }
                    break;

                case 3:
                    // Tambah stok obat
                    scanner.nextLine(); // Membersihkan buffer
                    System.out.print("Masukkan ID Obat: ");
                    String idTambah = scanner.nextLine();
                    System.out.print("Masukkan jumlah stok yang ingin ditambahkan: ");
                    int jumlahTambah = scanner.nextInt();

                        try (Connection conn = DatabaseConnection.getConnection()) {
                            String query = "UPDATE obat SET stok = stok + ? WHERE id_obat = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setInt(1, jumlahTambah);
                            stmt.setString(2, idTambah);
                            int rows = stmt.executeUpdate();

                            if (rows > 0) {
                                System.out.println("Stok berhasil ditambahkan.");
                            } else {
                                System.out.println("Obat dengan ID tersebut tidak ditemukan.");
                            }
                        } catch (SQLException e) {
                            System.out.println("Gagal menambahkan stok: " + e.getMessage());
                        }
                        break;

                    case 4:
                    //Kurangi Stok Obat
                        scanner.nextLine(); // Membersihkan buffer
                        System.out.print("Masukkan ID Obat: ");
                        String idKurang = scanner.nextLine();
                        System.out.print("Masukkan jumlah stok yang ingin dikurangi: ");
                        int jumlahKurang = scanner.nextInt();

                        try (Connection conn = DatabaseConnection.getConnection()) {
                            String query = "UPDATE obat SET stok = stok - ? WHERE id_obat = ? AND stok >= ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setInt(1, jumlahKurang);
                            stmt.setString(2, idKurang);
                            stmt.setInt(3, jumlahKurang);
                            int rows = stmt.executeUpdate();

                            if (rows > 0) {
                                System.out.println("Stok berhasil dikurangi.");
                            } else {
                                System.out.println("Stok tidak mencukupi atau obat dengan ID tersebut tidak ditemukan.");
                            }
                        } catch (SQLException e) {
                            System.out.println("Gagal mengurangi stok: " + e.getMessage());
                        }
                        break;

                        case 5:
                        //Update Data Obat
                        scanner.nextLine(); // Membersihkan buffer
                        System.out.print("Masukkan ID Obat yang akan diubah: ");
                        String idUpdate = scanner.nextLine();
                        System.out.println("Apa yang ingin diubah?");
                        System.out.println("1. ID Obat");
                        System.out.println("2. Nama Obat");
                        System.out.println("3. Stok Obat");
                        System.out.println("4. Tanggal Kadaluarsa");
                        System.out.print("Pilih opsi: ");
                        int opsiUpdate = scanner.nextInt();
                        scanner.nextLine(); // Membersihkan buffer
                    
                        try (Connection conn = DatabaseConnection.getConnection()) {
                            String query = "";
                            PreparedStatement stmt;
                    
                            switch (opsiUpdate) {
                                case 1:
                                    System.out.print("Masukkan ID Obat baru: ");
                                    String idBaru = scanner.nextLine();
                                    query = "UPDATE obat SET id_obat = ? WHERE id_obat = ?";
                                    stmt = conn.prepareStatement(query);
                                    stmt.setString(1, idBaru);
                                    stmt.setString(2, idUpdate);
                                    break;
                    
                                case 2:
                                    System.out.print("Masukkan Nama Obat baru: ");
                                    String namaBaru = scanner.nextLine();
                                    query = "UPDATE obat SET nama_obat = ? WHERE id_obat = ?";
                                    stmt = conn.prepareStatement(query);
                                    stmt.setString(1, namaBaru);
                                    stmt.setString(2, idUpdate);
                                    break;
                    
                                case 3:
                                    System.out.print("Masukkan Stok baru: ");
                                    int stokBaru = scanner.nextInt();
                                    query = "UPDATE obat SET stok = ? WHERE id_obat = ?";
                                    stmt = conn.prepareStatement(query);
                                    stmt.setInt(1, stokBaru);
                                    stmt.setString(2, idUpdate);
                                    break;
                    
                                case 4:
                                    System.out.print("Masukkan Tanggal Kadaluarsa baru (yyyy-MM-dd): ");
                                    String tanggalBaru = scanner.nextLine();
                                    query = "UPDATE obat SET tanggal_kadaluarsa = ? WHERE id_obat = ?";
                                    stmt = conn.prepareStatement(query);
                                    stmt.setString(1, tanggalBaru);
                                    stmt.setString(2, idUpdate);
                                    break;
                    
                                default:
                                    System.out.println("Opsi tidak valid.");
                                    continue;
                                }
                    
                                    int rowsUpdated = stmt.executeUpdate();
                                    if (rowsUpdated > 0) {
                                        System.out.println("Data berhasil diperbarui.");
                                    } else {
                                        System.out.println("ID Obat tidak ditemukan.");
                                    }
                                } catch (SQLException e) {
                                    System.out.println("Gagal memperbarui data: " + e.getMessage());
                                }
                                    break;
                        case 6:
                        //Delete Data Obat
                        scanner.nextLine(); // Membersihkan buffer
                        System.out.print("Masukkan ID Obat yang akan dihapus: ");
                        String idHapus = scanner.nextLine();

                        try (Connection conn = DatabaseConnection.getConnection()) {
                            String query = "DELETE FROM obat WHERE id_obat = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, idHapus);

                            int rowsDeleted = stmt.executeUpdate();
                        if (rowsDeleted > 0) {
                            System.out.println("Data obat berhasil dihapus.");
                        } else {
                            System.out.println("ID Obat tidak ditemukan.");
                        }
                        } catch (SQLException e) {
                            System.out.println("Gagal menghapus data: " + e.getMessage());
                        }
                        break;
                        case 7:
                        //Keluar dari Prog
                            System.out.println("Terima kasih telah menggunakan sistem manajemen stok obat.");
                        scanner.close();
                            System.exit(0);
                        break;
            }
        }
    }
}
                        

                    
                
            
        
    
                