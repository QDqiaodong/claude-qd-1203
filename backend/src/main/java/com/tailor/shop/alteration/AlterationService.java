package com.tailor.shop.alteration;

import com.tailor.shop.common.BizException;
import com.tailor.shop.common.Dict;
import com.tailor.shop.garment.Garment;
import com.tailor.shop.garment.GarmentRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AlterationService {

    private final AlterationRepository repo;
    private final GarmentRepository garmentRepo;

    public AlterationService(AlterationRepository repo, GarmentRepository garmentRepo) {
        this.repo = repo;
        this.garmentRepo = garmentRepo;
    }

    public List<Alteration> list() {
        return repo.findAll();
    }

    public List<Alteration> listByGarment(Long garmentId) {
        return repo.findByGarmentId(garmentId);
    }

    public Alteration create(Alteration input) {
        if (input.garmentId == null) {
            throw new BizException("改衣工单必须关联一件衣物");
        }
        Garment g = garmentRepo.findById(input.garmentId)
                .orElseThrow(() -> new BizException("关联衣物不存在：" + input.garmentId));
        if (!Dict.isValidAlterItem(input.item)) {
            throw new BizException("改衣项目不合法：" + input.item);
        }
        if (input.price == null || input.price <= 0) {
            throw new BizException("改衣报价必须大于 0");
        }
        Alteration saved = new Alteration();
        saved.garmentId = input.garmentId;
        saved.item = input.item;
        saved.price = input.price;
        saved.status = "待开工";
        return repo.save(saved);
    }

    public Alteration update(Long id, Alteration input) {
        Alteration a = repo.findById(id).orElseThrow(() -> new BizException("改衣工单不存在：" + id));
        if (input.item != null && !input.item.isBlank()) {
            if (!Dict.isValidAlterItem(input.item)) {
                throw new BizException("改衣项目不合法：" + input.item);
            }
            a.item = input.item;
        }
        if (input.price != null) {
            if (input.price <= 0) {
                throw new BizException("改衣报价必须大于 0");
            }
            a.price = input.price;
        }
        if (input.status != null && !input.status.isBlank()) {
            completeIfNeeded(a, input.status);
        }
        return repo.save(a);
    }

    /** 关系面：未填项目禁完成 */
    public Alteration complete(Long id) {
        Alteration a = repo.findById(id).orElseThrow(() -> new BizException("改衣工单不存在：" + id));
        return completeIfNeeded(a, "已完成");
    }

    private Alteration completeIfNeeded(Alteration a, String to) {
        if (!"已完成".equals(to)) {
            a.status = to;
            return repo.save(a);
        }
        if (a.item == null || a.item.isBlank()) {
            throw new BizException("改衣项目未填写，禁止完成工单");
        }
        if (a.price == null || a.price <= 0) {
            throw new BizException("改衣报价为 0，禁止完成工单");
        }
        a.status = "已完成";
        return repo.save(a);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new BizException("改衣工单不存在：" + id);
        }
        repo.deleteById(id);
    }
}
