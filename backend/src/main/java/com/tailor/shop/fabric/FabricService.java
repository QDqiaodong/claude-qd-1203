package com.tailor.shop.fabric;

import com.tailor.shop.common.BizException;
import com.tailor.shop.common.Dict;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FabricService {

    private final FabricRepository repo;

    public FabricService(FabricRepository repo) {
        this.repo = repo;
    }

    public List<Fabric> list() {
        return repo.findAll();
    }

    public List<Fabric> listByCategory(String category) {
        return repo.findByCategory(category);
    }

    public Fabric create(Fabric input) {
        if (input.name == null || input.name.isBlank()) {
            throw new BizException("面料名称不能为空");
        }
        Fabric saved = new Fabric();
        saved.name = input.name;
        saved.category = (input.category == null || !Dict.isValidFabricCategory(input.category))
                ? "配线" : input.category;
        saved.stock = (input.stock == null) ? 0 : input.stock;
        saved.unit = (input.unit == null || input.unit.isBlank()) ? "件" : input.unit;
        saved.threshold = (input.threshold == null) ? 0 : input.threshold;
        return repo.save(saved);
    }

    public Fabric update(Long id, Fabric input) {
        Fabric f = repo.findById(id).orElseThrow(() -> new BizException("面料不存在：" + id));
        if (input.name != null && !input.name.isBlank()) {
            f.name = input.name;
        }
        if (input.category != null && !input.category.isBlank()) {
            if (!Dict.isValidFabricCategory(input.category)) {
                throw new BizException("面料类别不合法：" + input.category);
            }
            f.category = input.category;
        }
        if (input.stock != null) {
            f.stock = input.stock;
        }
        if (input.unit != null && !input.unit.isBlank()) {
            f.unit = input.unit;
        }
        if (input.threshold != null) {
            f.threshold = input.threshold;
        }
        return repo.save(f);
    }

    /** 关系面：领用减库存，库存为负 / 低于下限拦截 */
    public Fabric consume(Long id, int qty) {
        Fabric f = repo.findById(id).orElseThrow(() -> new BizException("面料不存在：" + id));
        if (qty <= 0) {
            throw new BizException("领用数量必须大于 0");
        }
        if (f.stock == null || f.stock < qty) {
            throw new BizException("面料「" + f.name + "」库存不足，无法领用");
        }
        int after = f.stock - qty;
        if (after < (f.threshold == null ? 0 : f.threshold)) {
            throw new BizException("面料「" + f.name + "」领用后低于安全库存下限（" + f.threshold + "），已拦截");
        }
        f.stock = after;
        return repo.save(f);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new BizException("面料不存在：" + id);
        }
        repo.deleteById(id);
    }
}
