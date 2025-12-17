package com.gio.buku_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gio.buku_service.model.Buku;
import com.gio.buku_service.repository.BukuRepository;

@Service
public class BukuService {
  @Autowired
  private BukuRepository bukuRepository;

  public List<Buku> getAllBuku(){
    return bukuRepository.findAll();
  }

  public Buku getBukuById(Long id){
    return bukuRepository.findById(id).orElse(null);
  }

  public Buku createBuku(Buku buku){
    return bukuRepository.save(buku);
  }

  public Buku updateBuku(Long id, Buku bukuDetails){
    return bukuRepository.findById(id)
      .map(buku -> {
        // Update hanya field yang tidak null
        if (bukuDetails.getJudul() != null) {
          buku.setJudul(bukuDetails.getJudul());
        }
        if (bukuDetails.getPenerbit() != null) {
          buku.setPenerbit(bukuDetails.getPenerbit());
        }
        if (bukuDetails.getTahun_terbit() != null) {
          buku.setTahun_terbit(bukuDetails.getTahun_terbit());
        }
        return bukuRepository.save(buku);
      })
      .orElse(null);
  }

  public void deleteBuku(Long id){
    bukuRepository.deleteById(id);
  }
}

