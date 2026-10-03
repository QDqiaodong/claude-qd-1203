package com.tailor.shop.appointment;

import com.tailor.shop.alteration.Alteration;
import com.tailor.shop.alteration.AlterationRepository;
import com.tailor.shop.common.BizException;
import com.tailor.shop.common.Dict;
import com.tailor.shop.garment.Garment;
import com.tailor.shop.garment.GarmentRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private final AppointmentRepository repo;
    private final GarmentRepository garmentRepo;
    private final AlterationRepository alterationRepo;

    public AppointmentService(AppointmentRepository repo, GarmentRepository garmentRepo,
            AlterationRepository alterationRepo) {
        this.repo = repo;
        this.garmentRepo = garmentRepo;
        this.alterationRepo = alterationRepo;
    }

    public List<Appointment> list() {
        return repo.findAll();
    }

    public Appointment create(Appointment input) {
        if (input.garmentId == null) {
            throw new BizException("预约必须关联一件衣物");
        }
        Garment g = garmentRepo.findById(input.garmentId)
                .orElseThrow(() -> new BizException("关联衣物不存在：" + input.garmentId));
        Appointment saved = new Appointment();
        saved.garmentId = input.garmentId;
        saved.customer = (input.customer == null || input.customer.isBlank()) ? g.customer : input.customer;
        saved.appointTime = (input.appointTime == null) ? LocalDate.now() : input.appointTime;
        saved.paid = Boolean.FALSE;
        saved.status = "已预约";
        return repo.save(saved);
    }

    public Appointment update(Long id, Appointment input) {
        Appointment a = repo.findById(id).orElseThrow(() -> new BizException("预约记录不存在：" + id));
        if (input.customer != null && !input.customer.isBlank()) {
            a.customer = input.customer;
        }
        if (input.appointTime != null) {
            a.appointTime = input.appointTime;
        }
        if (input.paid != null) {
            a.paid = input.paid;
        }
        return repo.save(a);
    }

    /** 关系面：取衣须待取且工单完成、须付清 */
    public Appointment pickup(Long id) {
        Appointment a = repo.findById(id).orElseThrow(() -> new BizException("预约记录不存在：" + id));
        if ("已取衣".equals(a.status)) {
            throw new BizException("该预约已取衣，不能重复取衣");
        }
        Garment g = garmentRepo.findById(a.garmentId)
                .orElseThrow(() -> new BizException("关联衣物不存在：" + a.garmentId));
        if (!"待取".equals(g.status)) {
            throw new BizException("衣物状态为「" + g.status + "」，须为「待取」方可取衣");
        }
        List<Alteration> alters = alterationRepo.findByGarmentId(g.id);
        if (alters.isEmpty()) {
            throw new BizException("该衣物尚无任何改衣工单，无法取衣");
        }
        boolean allDone = alters.stream().allMatch(x -> "已完成".equals(x.status));
        if (!allDone) {
            throw new BizException("该衣物仍有未完成的改衣工单，须全部完成后方可取衣");
        }
        if (!Boolean.TRUE.equals(a.paid)) {
            throw new BizException("该预约尚未付清，付清后方可取衣");
        }
        a.pickupTime = LocalDate.now();
        a.status = "已取衣";
        repo.save(a);
        g.status = "已取";
        garmentRepo.save(g);
        return a;
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new BizException("预约记录不存在：" + id);
        }
        repo.deleteById(id);
    }
}
