package com.gio.anggota_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gio.anggota_service.model.Anggota;
import com.gio.anggota_service.repository.AnggotaRepository;

@Service
public class AnggotaService {
  @Autowired
  private AnggotaRepository anggotaRepository;

  public List<Anggota> getAllAnggota(){
    return anggotaRepository.findAll();
  }

  public Anggota getAnggotaById(Long id){
    return anggotaRepository.findById(id).orElse(null);
  }

  public Anggota createAnggota(Anggota anggota){
    return anggotaRepository.save(anggota);
  }

  public Anggota updateAnggota(Long id, Anggota anggotaDetails){
    return anggotaRepository.findById(id)
      .map(anggota -> {
        // Update hanya field yang tidak null
        if (anggotaDetails.getNim() != null) {
          anggota.setNim(anggotaDetails.getNim());
        }
        if (anggotaDetails.getNama() != null) {
          anggota.setNama(anggotaDetails.getNama());
        }
        if (anggotaDetails.getAlamat() != null) {
          anggota.setAlamat(anggotaDetails.getAlamat());
        }
        if (anggotaDetails.getEmail() != null) {
          anggota.setEmail(anggotaDetails.getEmail());
        }
        if (anggotaDetails.getJenis_kelamin() != null) {
          anggota.setJenis_kelamin(anggotaDetails.getJenis_kelamin());
        }
        return anggotaRepository.save(anggota);
      })
      .orElse(null);
  }

  public void deleteAnggota(Long id){
    anggotaRepository.deleteById(id);
  }
}