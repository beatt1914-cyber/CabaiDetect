package com.example.cabaidetect.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendOtpEmail(String toEmail, String namaLengkap, String otp) {
        try {
            if (fromEmail == null || fromEmail.contains("your-email@gmail.com") || fromEmail.isBlank()) {
                System.out.println("=================================================");
                System.out.println(">>> [DEV OTP] Untuk: " + toEmail + " | KODE OTP: " + otp);
                System.out.println("=================================================");
                return false;
            }
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail, "CabaiDetect");
            helper.setTo(toEmail);
            helper.setSubject("Kode OTP Verifikasi CabaiDetect");
            helper.setText(buildOtpHtml(namaLengkap, otp), true);
            mailSender.send(msg);
            return true;
        } catch (Exception e) {
            System.err.println("Gagal mengirim email OTP via SMTP: " + e.getMessage());
            System.out.println("=================================================");
            System.out.println(">>> [FALLBACK OTP] Untuk: " + toEmail + " | KODE OTP: " + otp);
            System.out.println("=================================================");
            return false;
        }
    }

    public void sendWelcomeEmail(String toEmail, String namaLengkap) {
        try {
            if (fromEmail == null || fromEmail.contains("your-email@gmail.com") || fromEmail.isBlank()) {
                return;
            }
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(fromEmail, "CabaiDetect");
            helper.setTo(toEmail);
            helper.setSubject("Selamat Datang di CabaiDetect! 🌶️");
            helper.setText(buildWelcomeHtml(namaLengkap), true);
            mailSender.send(msg);
        } catch (Exception e) {
            // Welcome email non-critical, log only
            System.err.println("Gagal kirim welcome email: " + e.getMessage());
        }
    }

    private String buildOtpHtml(String nama, String otp) {
        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"/></head>
            <body style="margin:0;padding:0;background:#0f172a;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0">
                <tr><td align="center" style="padding:40px 20px;">
                  <table width="560" style="background:#1e293b;border-radius:16px;overflow:hidden;">
                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#dc2626,#991b1b);padding:32px;text-align:center;">
                        <div style="font-size:40px;">🌶️</div>
                        <h1 style="color:#fff;margin:8px 0 0;font-size:24px;font-weight:700;">CabaiDetect</h1>
                        <p style="color:#fca5a5;margin:4px 0 0;font-size:13px;">Sistem Deteksi Penyakit Cabe</p>
                      </td>
                    </tr>
                    <!-- Body -->
                    <tr>
                      <td style="padding:36px 40px;">
                        <p style="color:#94a3b8;font-size:14px;margin:0 0 8px;">Halo,</p>
                        <h2 style="color:#f1f5f9;font-size:20px;margin:0 0 16px;">%s 👋</h2>
                        <p style="color:#94a3b8;font-size:14px;line-height:1.6;margin:0 0 28px;">
                          Kamu meminta kode OTP untuk verifikasi akun CabaiDetect.<br/>
                          Gunakan kode di bawah ini (berlaku <strong style="color:#f1f5f9;">10 menit</strong>):
                        </p>
                        <!-- OTP Box -->
                        <div style="background:#0f172a;border:2px solid #dc2626;border-radius:12px;padding:24px;text-align:center;margin:0 0 28px;">
                          <div style="letter-spacing:12px;font-size:36px;font-weight:800;color:#ef4444;font-family:monospace;">%s</div>
                          <p style="color:#475569;font-size:12px;margin:8px 0 0;">Jangan bagikan kode ini kepada siapapun</p>
                        </div>
                        <p style="color:#475569;font-size:13px;margin:0;">
                          Jika kamu tidak meminta kode ini, abaikan email ini.
                        </p>
                      </td>
                    </tr>
                    <!-- Footer -->
                    <tr>
                      <td style="background:#0f172a;padding:20px 40px;text-align:center;border-top:1px solid #1e293b;">
                        <p style="color:#334155;font-size:12px;margin:0;">© 2024 CabaiDetect. All rights reserved.</p>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(nama, otp);
    }

    private String buildWelcomeHtml(String nama) {
        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"/></head>
            <body style="margin:0;padding:0;background:#0f172a;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0">
                <tr><td align="center" style="padding:40px 20px;">
                  <table width="560" style="background:#1e293b;border-radius:16px;overflow:hidden;">
                    <tr>
                      <td style="background:linear-gradient(135deg,#dc2626,#991b1b);padding:32px;text-align:center;">
                        <div style="font-size:40px;">🌶️</div>
                        <h1 style="color:#fff;margin:8px 0 0;font-size:24px;font-weight:700;">Selamat Datang!</h1>
                      </td>
                    </tr>
                    <tr>
                      <td style="padding:36px 40px;">
                        <h2 style="color:#f1f5f9;font-size:20px;margin:0 0 16px;">Halo, %s! 🎉</h2>
                        <p style="color:#94a3b8;font-size:14px;line-height:1.6;margin:0 0 20px;">
                          Akun CabaiDetect kamu berhasil diverifikasi!<br/>
                          Mulai deteksi penyakit cabe dengan mudah menggunakan AI kami.
                        </p>
                        <div style="background:#0f172a;border-radius:12px;padding:20px;margin:0 0 20px;">
                          <p style="color:#94a3b8;font-size:13px;margin:0 0 8px;">✅ Foto cabe → Deteksi otomatis</p>
                          <p style="color:#94a3b8;font-size:13px;margin:0 0 8px;">✅ Cabe Rawit, Merah Panjang, Setan</p>
                          <p style="color:#94a3b8;font-size:13px;margin:0;">✅ Riwayat deteksi tersimpan</p>
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <td style="background:#0f172a;padding:20px 40px;text-align:center;">
                        <p style="color:#334155;font-size:12px;margin:0;">© 2024 CabaiDetect</p>
                      </td>
                    </tr>
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(nama);
    }
}
