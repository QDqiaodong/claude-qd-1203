package com.tailor.shop.garment;

import com.tailor.shop.common.BizException;
import com.tailor.shop.common.Dict;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GarmentService {

    private final GarmentRepository repo;

    public GarmentService(GarmentRepository repo) {
        this.repo = repo;
    }

    public List<Garment> list() {
        return repo.findAll();
    }

    public List<Garment> listByStatus(String status) {
        return repo.findByStatus(status);
    }

    public Garment create(Garment input) {
        Garment saved = new Garment();
        if (input.code == null || input.code.isBlank()) {
            throw new BizException("衣物编号不能为空");
        }
        if (repo.findByCode(input.code).isPresent()) {
            throw new BizException("衣物编号已存在：" + input.code);
        }
        saved.code = input.code;
        saved.customer = (input.customer == null || input.customer.isBlank()) ? "散客" : input.customer;
        if (!Dict.isValidGarmentCategory(input.category)) {
            throw new BizException("衣物品类不合法：" + input.category);
        }
        saved.category = input.category;
        saved.material = (input.material == null || input.material.isBlank()) ? "未注明" : input.material;
        saved.status = "待改";
        return repo.save(saved);
    }

    public Garment update(Long id, Garment input) {
        Garment g = repo.findById(id).orElseThrow(() -> new BizException("衣物不存在：" + id));
        if (input.code != null) {
            g.code = input.code;
        }
        if (input.customer != null && !input.customer.isBlank()) {
            g.customer = input.customer;
        }
        if (input.category != null && !input.category.isBlank()) {
            if (!Dict.isValidGarmentCategory(input.category)) {
                throw new BizException("衣物品类不合法：" + input.category);
            }
            g.category = input.category;
        }
        if (input.material != null && !input.material.isBlank()) {
            g.material = input.material;
        }
        if (input.status != null && !input.status.isBlank()) {
            transferStatus(g, input.status);
        }
        return repo.save(g);
    }

    /** 关系面：状态流转，已取不可回退 */
    public Garment transferStatus(Garment g, String to) {
        if (!Dict.isValidGarmentStatus(to)) {
            throw new BizException("衣物状态不合法：" + to);
        }
        if (!Dict.canTransferGarment(g.status, to)) {
            throw new BizException("衣物状态不可由「" + g.status + "」流转为「" + to + "」（已取不可回退）");
        }
        g.status = to;
        return repo.save(g);
    }

    public Garment changeStatus(Long id, String to) {
        Garment g = repo.findById(id).orElseThrow(() -> new BizException("衣物不存在：" + id));
        return transferStatus(g, to);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new BizException("衣物不存在：" + id);
        }
        repo.deleteById(id);
    }
}
