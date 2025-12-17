package com.gio.peminjaman_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.gio.peminjaman_service.model.Peminjaman;
import com.gio.peminjaman_service.repository.PeminjamanRepository;
import com.gio.peminjaman_service.vo.Anggota;
import com.gio.peminjaman_service.vo.Buku;
import com.gio.peminjaman_service.vo.ResponseTemplate;

@Service
public class PeminjamanService {

  private final RabbitTemplate rabbitTemplate;

  private static final Logger log = LoggerFactory.getLogger(PeminjamanService.class);

  @Value("${app.rabbitmq-peminjaman.exchange}")
  private String exchange;

  @Value("${app.rabbitmq-peminjaman.routing-key}")
  private String routingKey;

  @Autowired
  private PeminjamanRepository peminjamanRepository;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private DiscoveryClient discoveryClient;

  public PeminjamanService(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public List<Peminjaman> getAllPeminjaman(){
    return peminjamanRepository.findAll();
  }

  public Peminjaman getPeminjamanById(Long id){
    return peminjamanRepository.findById(id).orElse(null);
  }

  public List<ResponseTemplate> getPeminjamanWithAnggotaBukuById(Long id){
    List<ResponseTemplate> responseTemplates = new ArrayList<>();
    Peminjaman peminjaman = getPeminjamanById(id);

    if (peminjaman == null) {
      return null;
    }

    ServiceInstance serviceInstance = discoveryClient.getInstances("API-GATEWAY-PUSTAKA").get(0);

    Anggota anggota = restTemplate.getForObject(serviceInstance.getUri() + "/api/anggota/" + peminjaman.getAnggotaId(), Anggota.class);

    Buku buku = restTemplate.getForObject(serviceInstance.getUri() + "/api/buku/" + peminjaman.getBukuId(), Buku.class);

    ResponseTemplate vo = new ResponseTemplate();
    vo.setPeminjaman(peminjaman);
    vo.setAnggota(anggota);
    vo.setBuku(buku);

    responseTemplates.add(vo);

    return responseTemplates;
  }

  public Peminjaman createPeminjaman(Peminjaman peminjaman){
    Peminjaman savedPeminjaman = peminjamanRepository.save(peminjaman);
    // rabbitTemplate.convertAndSend(exchange, routingKey, savedPeminjaman);

    // log.info("📩 Message sent to exchange [{}] with routing key [{}], Payload: {}", 
    //             exchange, routingKey, savedPeminjaman);
    return savedPeminjaman;
  }

    public Peminjaman updatePeminjaman(Long id, Peminjaman peminjamanDetails){
    return peminjamanRepository.findById(id)
      .map(peminjaman -> {
        // Update hanya field yang tidak null
        if (peminjamanDetails.getTanggalPinjam() != null) {
          peminjaman.setTanggalPinjam(peminjamanDetails.getTanggalPinjam());
        }
        if (peminjamanDetails.getTanggalKembali() != null) {
          peminjaman.setTanggalKembali(peminjamanDetails.getTanggalKembali());
        }
        if (peminjamanDetails.getAnggotaId() != null) {
          peminjaman.setAnggotaId(peminjamanDetails.getAnggotaId());
        }
        if (peminjamanDetails.getBukuId() != null) {
          peminjaman.setBukuId(peminjamanDetails.getBukuId());
        }
        return peminjamanRepository.save(peminjaman);
      })
      .orElse(null);
  }

  public void deletePeminjaman(Long id){
    peminjamanRepository.deleteById(id);
  }
}
