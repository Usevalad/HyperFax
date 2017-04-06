package com.unnamed.b.atv.sample.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.sample.ModelList;
import com.unnamed.b.atv.sample.MyApplication;
import com.unnamed.b.atv.sample.PostModel;
import com.unnamed.b.atv.sample.R;
import com.unnamed.b.atv.sample.holder.IconTreeItemHolder;
import com.unnamed.b.atv.view.AndroidTreeView;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends ActionBarActivity {

    //    private TextView statusBar;
    private final String TAG = "MainActivity";
    private AndroidTreeView tView;
    private Context mContext;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_default);
//        mTextView = (TextView) findViewById(R.id.text);


        MyApplication.getApi().getData(new PostModel("21605da16ca43db7f8f2ff6ede1129ea"))
                .enqueue(new Callback<ModelList>() {
            @Override
            public void onResponse(Call<ModelList> call, Response<ModelList> response) {
                //Данные успешно пришли, но надо проверить response.body() на null
                Toast.makeText(mContext, "onResponse", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onResponse");

                if (response.body() != null) {
                    Log.d(TAG, "onResponse: body != null");
                    Toast.makeText(mContext, response.body().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ModelList> call, Throwable t) {
                Log.wtf(TAG, "onFailure", t);
                //Произошла ошибка
                Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
            }
        });


        mContext = getApplicationContext();

//        statusBar = (TextView) findViewById(R.id.status_bar);
        ViewGroup containerView = (ViewGroup) findViewById(R.id.container);

        TreeNode root = TreeNode.root();
        TreeNode parcels = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Посылки 5000"));
        TreeNode parishFunds = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Приход денежных средств 1000"));
        TreeNode arrivalOfGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Приход товара 3000"));
        TreeNode cashOutflow = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Расход денежных средств 2000"));
        TreeNode productConsumption = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_laptop, "Расход товара 4000"));

        TreeNode inBox = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Входящие 5001"));
        TreeNode fromDropShippingBuyers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "От дроповых покупателей 5370"));
        TreeNode refunds = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты 5371"));
        TreeNode money = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Деньги 5374"));
        TreeNode exchanges = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Обмены 5372"));
        TreeNode renouncement = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Отказ 5373"));
        fromDropShippingBuyers.addChildren(refunds, money, exchanges, renouncement);

        TreeNode fromSomeOne = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "От кого-то 5770"));
        TreeNode notTargetLoadsFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Не целевые грузы (личные) 5771"));
        fromSomeOne.addChildren(notTargetLoadsFROM);

        TreeNode fromWholesaleBuyers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "От поставщиков 5170"));
        TreeNode refundsRealizableGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты реализационного товара 5382"));
        TreeNode refundsDefectiveGoodsFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты уже купленного брака 5381"));
        TreeNode exchangeOfGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Обмены купленного на другой 5383"));
        fromWholesaleBuyers.addChildren(refundsRealizableGoods, refundsDefectiveGoodsFROM, exchangeOfGoods);

        TreeNode fromSuppliers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "От оптовых покупателей 5380"));
        TreeNode documentsFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Документы 5173"));
        TreeNode sampleFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Образцы 5172"));
        TreeNode deliveryFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Поставки 5171"));
        fromSuppliers.addChildren(documentsFROM, sampleFROM, deliveryFROM);


        TreeNode fromRetailCustomers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "От розничных покупателей 5270"));
        TreeNode retailRefunds = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты 5271"));
        TreeNode retailMoney = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Деньги 5274"));
        TreeNode retailExchanges = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Обмены 5272"));
        TreeNode retailRenouncement = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Отказ 5273"));
        fromRetailCustomers.addChildren(retailRefunds, retailMoney, retailExchanges, retailRenouncement);

        inBox.addChildren(fromDropShippingBuyers, fromSomeOne, fromWholesaleBuyers, fromSuppliers, fromRetailCustomers);

        TreeNode outBox = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Исходящие 5002"));

        TreeNode toFutureCounterparties = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "К будущим контрагентам 5390"));
        TreeNode samplesOfGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Образцы продукции и тд. 5391"));
        toFutureCounterparties.addChildren(samplesOfGoods);

        TreeNode toDropShippingBuyers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "К дроповым покупателям 5320"));
        TreeNode goodsExchange = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Обмен товара 5322"));
        TreeNode goods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Товары 5321"));
        toDropShippingBuyers.addChildren(goodsExchange, goods);

        TreeNode toSomeOne = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "К кому-либо 5720"));
        TreeNode notTargetLoadsTO = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Не целевые грузы (личные) 5721"));
        toSomeOne.addChildren(notTargetLoadsTO);

        TreeNode toWholesaleBuyers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "К оптовым покупателям 5330"));
        TreeNode goodsExchangeTO = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Обмены купленного или брака 5332"));
        TreeNode deliveryTO = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Товары (поставки) 5331"));
        toWholesaleBuyers.addChildren(goodsExchangeTO, deliveryTO);

        TreeNode toSuppliers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "К поставщикам 5120"));
        TreeNode refundsDefectiveGoodsTO = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возврат брака 5121"));
        TreeNode refundsDocumentsTO = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты документов 5124"));
        TreeNode refundsSamplesTO = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты образцов 5122"));
        TreeNode refundsMassTO = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты товарных масс 5123"));
        toSuppliers.addChildren(refundsDefectiveGoodsTO, refundsDocumentsTO, refundsSamplesTO, refundsMassTO);

        TreeNode toRetailCustomers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "К розничным покупателям 5220"));
        TreeNode goodsExchangeRetail = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Обмен товара 5222"));
        TreeNode goodsTO = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Товары 5221"));
        toRetailCustomers.addChildren(goodsExchangeRetail, goodsTO);

        outBox.addChildren(toFutureCounterparties, toDropShippingBuyers, toSomeOne, toWholesaleBuyers, toSuppliers, toRetailCustomers);

        TreeNode movements = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Перемещения 5600"));
        TreeNode refundsSurpluses = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возврат излишков 5671"));
        TreeNode materialValues = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Материальные ценности 5623"));
        TreeNode notTarget = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Не целевые грузы 5675"));
        TreeNode documentsPackages = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Пакеты документов 5622"));
        TreeNode goodsDelivery = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Поставки товаров 5621"));
        TreeNode partsOfPrefabricatedPremises = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Части сборных посылок 5672"));
        movements.addChildren(refundsSurpluses, materialValues, notTarget, documentsPackages, goodsDelivery, partsOfPrefabricatedPremises);

        parcels.addChildren(inBox, outBox, movements);

        TreeNode refundFromSupplier = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возврат средств от поставщика 1100"));

        TreeNode proceedsOfSales = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Выручка от продаж 1200"));
        TreeNode retailStores = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи розничных магазинов 1210"));
        TreeNode supplementCertificate = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Доплата к сертификату 1215"));
        TreeNode salesForCashNoCheck = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи за наличные по кассе без чека 1213"));
        TreeNode salesForCashWithCheck = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи за наличные с товарным чеком 1212"));
        TreeNode salesForCashDiscount = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи за наличные со скидкой 1211"));
        TreeNode salesForCard = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи через терминал 1214"));
        retailStores.addChildren(supplementCertificate, salesForCashNoCheck, salesForCashWithCheck, salesForCashDiscount, salesForCard);

        TreeNode centralOffice = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи центрального офиса 1230"));
        TreeNode vk = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи группы ВК 1232"));
        TreeNode callCentre = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи кол-центра 1231"));
        TreeNode dropAndWholeSale = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи опт/дроп 1233"));
        TreeNode site = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажи сайта 1234"));
        centralOffice.addChildren(vk, callCentre, dropAndWholeSale, site);

        proceedsOfSales.addChildren(retailStores, centralOffice);

        TreeNode movementInOrganization = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Движение в организации 1600"));
        TreeNode innerTransactions = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Внутренние транзакции входящие 1601"));
        TreeNode refundEmployee = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возврат средств сотрудником 1602"));
        TreeNode collectionCard = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Инкассация наличных на карту 1605"));
        TreeNode transfersFromOtherCards = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Переводы с других карт организации 1604"));
        TreeNode admissionToAcquiringAccount = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Поступление на эквайринговый счет 1606"));
        TreeNode arrivingExternalAccount = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Поступление с внешнего счета 1603"));
        movementInOrganization.addChildren(innerTransactions, refundEmployee, collectionCard, transfersFromOtherCards, admissionToAcquiringAccount, arrivingExternalAccount);

        TreeNode nonCoreIncome = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Доход от неосновной деятельности 1300"));
        TreeNode services = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Предоставление услуг 1302"));
        TreeNode assetsSale = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажа основных средств 1301"));
        nonCoreIncome.addChildren(services, assetsSale);

        TreeNode arrivalOfFounders = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Приход инвестиций учредителей 1510"));

        parishFunds.addChildren(refundFromSupplier, proceedsOfSales, movementInOrganization, nonCoreIncome, arrivalOfFounders);

        TreeNode refund = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возврат 3260"));
        TreeNode defectiveGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Дефект на товаре 3273"));
        TreeNode wrongSize = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Другой размер (нет обмена) 3271"));
        TreeNode notMatchPhoto = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Не соответствует фото 3272"));
        refund.addChildren(defectiveGoods, wrongSize, notMatchPhoto);

        TreeNode exchange = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Обмен 3230"));
        TreeNode otherSize = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Другой размер 3242"));
        TreeNode otherGood = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Другой товар 3241"));
        TreeNode otherColor = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Другой цвет 3243"));
        TreeNode saleError = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка проведения 3244"));
        exchange.addChildren(otherSize, otherGood, otherColor, saleError);

        TreeNode posting = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Оприходование 3620"));
        TreeNode kindError = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка пересортицы 3622"));
        TreeNode writeOffError = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка списания 3621"));
        posting.addChildren(kindError, writeOffError);

        TreeNode fromEmployee = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "От сотрудника 3400"));

        TreeNode saleCancel = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Отмена продажи 3210"));
        TreeNode customerRejection = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Отказ клиента 3213"));
        TreeNode noSuchGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Отсутствует товар 3211"));
        TreeNode behaviorError = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка проведения 3212"));
        saleCancel.addChildren(customerRejection, noSuchGoods, behaviorError);
        fromEmployee.addChildren(saleCancel);

        TreeNode deliveryError = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка отгрузки 3220"));
        TreeNode goodsTransferError = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка передачи товара 3222"));
        TreeNode packerError = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка упаковщика 3221"));
        deliveryError.addChildren(goodsTransferError, packerError);

        TreeNode transfer = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Перемещение 3630"));
        TreeNode fixing = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Корректировка 3634"));
        TreeNode storeToStore = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Магазин-Магазин 3633"));
        TreeNode storeToStock = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Магазин-Склад 3632"));
        TreeNode stockToStore = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Склад-Магазин 3631"));
        transfer.addChildren(fixing, storeToStore, storeToStock, stockToStore);

        TreeNode otherKind = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Пересортица 3610"));
        TreeNode postingError = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка оприходования 3611"));
        otherKind.addChildren(postingError);

        TreeNode supply = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Поступление 3110"));
        TreeNode ransom = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Выкуп 3111"));
        TreeNode fixingSupply = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Корректировка 3113"));
        TreeNode retail = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Реализация 3112"));
        supply.addChildren(ransom, fixingSupply, retail);

        TreeNode other = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Прочее 3710"));
        arrivalOfGoods.addChildren(refund, exchange, posting, fromEmployee, deliveryError, transfer, otherKind, supply, other);

        TreeNode returnFoundersInvestments = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возврат инвестиций учредителей 2510"));

        TreeNode customerReturns = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты клиентам 2200"));
        TreeNode customersCardReturns = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты клиентам на карту 2202"));
        TreeNode customersCashReturns = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты клиентам наличными 2203"));
        TreeNode wholeSaleCustomersReturns = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возвраты клиентам опт 2201"));
        customerReturns.addChildren(customersCardReturns, customersCashReturns, wholeSaleCustomersReturns);

        TreeNode organizationMovement = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Движение в организации 2600"));
        TreeNode externalTransactions = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Внутренние транзакции исходящие 2601"));
        TreeNode externalAccountTransfer = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Выведение на внешний счет 2603"));
        TreeNode cashRecessForCollections = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Выемка наличных для инкассации 2605"));
        TreeNode cashRecessForEmployee = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Выемка средств сотрудником 2602"));
        TreeNode organizationOtherCardsTransactions = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Переводы на другие карты организации 2604"));
        organizationMovement.addChildren(externalTransactions, externalAccountTransfer, cashRecessForCollections,
                cashRecessForEmployee, organizationOtherCardsTransactions);

        TreeNode assetsPurchase = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Закупка основных средств 2370"));
        TreeNode library = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Библиотека 2377"));
        TreeNode cctv = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Видеонаблюдение 2374\n"));
        TreeNode tools = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Инструменты 2376"));
        TreeNode light = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Освещение 2375"));
        TreeNode officeEquipment = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Офисная техника 2371"));
        TreeNode saleFurniture = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Торговое оборудование 2372"));
        TreeNode photoVideoStudio = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Фото-видео-студия 2373"));
        assetsPurchase.addChildren(library, cctv, tools, light, officeEquipment, saleFurniture, photoVideoStudio);

        TreeNode paymentSuppliers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Оплата поставщикам 2100"));
        TreeNode madeGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "За произведенный товар 2104"));
        TreeNode soldGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "За реализационный товар 2102"));
        TreeNode ownGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "За собственный товар 2101"));
        paymentSuppliers.addChildren(madeGoods, soldGoods, ownGoods);

        TreeNode softwareDevelopment = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Разработка ПО 2390"));
        TreeNode oneC = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Разработка 1С 2394"));
        TreeNode crm = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Разработка CRM 2391"));
        TreeNode additionalModules = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Разработка дополнительных модулей 2393"));
        TreeNode cite = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Разработка сайта 2392"));
        softwareDevelopment.addChildren(oneC, crm, additionalModules, cite);

        TreeNode currentExpenditure = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Текущие расходы 2320"));
        TreeNode rent = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Аренда 2310"));
        TreeNode roomRent = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Аренда помещения 2311"));
        TreeNode communalPayments = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Коммунальные платежи 2312"));
        TreeNode otherRentCosts = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Прочие затраты на аренду 2313"));
        rent.addChildren(roomRent, communalPayments, otherRentCosts);

        TreeNode bank = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Банк 2350"));
        TreeNode monthlyFee = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ежемесячная оплата 2354"));
        TreeNode liqPayPercent = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Процент за LiqPay-оплаты 2355"));
        TreeNode cashWithdrawalPercent = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Процент за снятие наличных 2352"));
        TreeNode acquiringPercent = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Процент за эквайринг 2351"));
        TreeNode transferPercent = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Процент при переводе 2353"));
        bank.addChildren(monthlyFee, liqPayPercent, cashWithdrawalPercent, acquiringPercent, transferPercent);

        TreeNode delivery = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Доставка 2340"));
        TreeNode courier = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Курьер 2341"));

        TreeNode novaPoshta = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Новая Почта 2380"));
        TreeNode purchase = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Доставка закупки 2383"));
        TreeNode toCustomer = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Доставка клиенту 2381"));
        TreeNode betweenStores = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Доставка между магазинами 2382"));
        novaPoshta.addChildren(purchase, toCustomer, betweenStores);

        TreeNode taxi = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Такси 2342"));
        delivery.addChildren(courier, novaPoshta, taxi);

        TreeNode taxes = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Налоги 2700"));
        TreeNode war = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Военный сбор 2701"));
        TreeNode ediniy = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Единый налог 2702"));
        TreeNode esv = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "ЕСВ 2703"));
        TreeNode pdfo = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "ПДФО 2704"));
        taxes.addChildren(war, ediniy, esv, pdfo);

        TreeNode consumablesAndServices = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Расходные материалы и услуги 2800"));
        TreeNode internet = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Интернет 2801"));
        TreeNode officeStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Канцелярия 2807"));
        TreeNode food = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Кафетерий 2808"));
        TreeNode outOfTown = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Командировки: транспорт и суточные 2803"));
        TreeNode outOfTownEvents = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Корпоративные мероприятия 2804"));
        TreeNode mobile = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Мобильная связь 2802"));
        TreeNode otherStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Остальное 2815"));
        TreeNode office = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Расходы бухгалтерии и юридические услуги 2809"));
        TreeNode employee = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Расходы по подбору сотрудников 2813"));
        TreeNode officeRepairs = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ремонт в магазинах и офисах 2814"));
        TreeNode toolsRepairs = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ремонт и обслуживание техники 2805"));
        TreeNode citeRepairs = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Текущие расходы на сайт 2811"));
        toolsRepairs.addChildren(citeRepairs);

        TreeNode clean = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Текущие расходы по клинингу 2812"));
        TreeNode cleanStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Хоз.товары 2806"));
        consumablesAndServices.addChildren(internet, officeStuff, food, outOfTown, outOfTownEvents, mobile,
                otherStuff, office, employee, officeRepairs, toolsRepairs, clean, cleanStuff);


        TreeNode costsOfEmployee = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Расходы на персонал 2400"));
        TreeNode bonus = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Бонусная выплата зарплаты 2411"));
        TreeNode it = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Зарплата айтишников 2408"));
        TreeNode accountant = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Зарплата бухгалтеров 2405"));
        TreeNode purchaser = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Зарплата закупщиков 2407"));
        TreeNode internetSaler = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Зарплата интернет-продавцов 2406"));
        TreeNode marketolog = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Зарплата маркетологов 2404"));
        TreeNode manager = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Зарплата менеджеров 2409"));
        TreeNode saler = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Зарплата продавцов магазинов 2402"));
        TreeNode stockWorker = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Зарплата работников склада 2403"));
        TreeNode remuneration = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Партнерское вознагражение ФОП 2401"));
        costsOfEmployee.addChildren(bonus, it, accountant, purchaser, internetSaler, marketolog, manager,
                saler, stockWorker, remuneration);

        TreeNode advertisingCosts = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Расходы на рекламу 2900"));
        TreeNode internetAndMedia = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Реклама в интернете и медиа 2901"));
        TreeNode competition = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Конкурсная реклама 2907"));
        TreeNode context = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Контекстная реклама в Гугл 2906"));
        TreeNode instagram = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Реклама в Инстаграмм 2902"));
        TreeNode VK = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Реклама в сообществах ВК 2904"));
        TreeNode sms = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Реклама СМС-рассылкой 2903"));
        TreeNode target = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Реклама таргетированная Вконтакте 2905"));
        internetAndMedia.addChildren(competition, context, instagram, VK, sms, target);

        TreeNode external = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Реклама внешняя 2911"));
        TreeNode billboard = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Афиши в вагонах метро 2915"));
        TreeNode info = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Информационные стенды и щиты 2917"));
        TreeNode metroboard = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Метроборды и метролайты 2914"));
        TreeNode transport = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Наземный транспорт 2912"));
        TreeNode otherThings = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Остальная реклама 2923"));
        TreeNode leaflets = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Печать и раздача листовок 2922"));
        TreeNode billboardPrint = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Печать и расклейка афиш по городу 2916"));
        TreeNode shoppingCenter = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Рекламные плоскости в торговом центре 2921"));
        TreeNode cityLight = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ситилайты 2913"));
        TreeNode traffaret = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Трафареты на асфальте 2919"));
        TreeNode facade = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Фасадная реклама 2918"));
        external.addChildren(billboard, info, metroboard, transport, otherThings, leaflets, billboardPrint,
                shoppingCenter, cityLight, traffaret, facade);

        advertisingCosts.addChildren(internetAndMedia, external);


        currentExpenditure.addChildren(rent, bank, delivery, taxes, consumablesAndServices,
                costsOfEmployee, advertisingCosts);

        TreeNode foundersRecess = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Чистые выемки учредителей 2520"));

        cashOutflow.addChildren(returnFoundersInvestments, customerReturns, organizationMovement, assetsPurchase,
                paymentSuppliers, softwareDevelopment, currentExpenditure, foundersRecess);


        TreeNode returnSuppliers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возврат поставщику 4110"));
        TreeNode defective = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Брак товара 4112"));
        TreeNode virtualReturn = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Виртуальный возврат 4115"));
        TreeNode balancesReturn = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Возврат остатков 4114"));
        TreeNode endOfSeason = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Конец сезона 4111"));
        TreeNode noSales = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Нет продаж (не ликвид) 4113"));
        returnSuppliers.addChildren(defective, virtualReturn, balancesReturn, endOfSeason, noSales);


        TreeNode wage = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Выемка на ЗП 4501"));
        TreeNode otherKindStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Пересортица 4610"));
        TreeNode error = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка оприходования 4611"));
        otherKindStuff.addChildren(error);

        TreeNode discount = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажа со скидкой 4502"));
        TreeNode goodsSale = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Продажа товара 4200"));
        TreeNode defectiveThing = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Дефект 4214"));
        TreeNode exchange2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Обмен 4230"));
        TreeNode wrongSize2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Другой размер 4232"));
        TreeNode wrongGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Другой товар 4231"));
        TreeNode wrongColor2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Другой цвет 4233"));
        TreeNode postingError2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибка проведения 4234"));
        exchange2.addChildren(wrongSize2, wrongGoods, wrongColor2, postingError2);


        TreeNode certificateBy = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "По сертификату 4213"));
        TreeNode retail2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Реализация 4220"));
        TreeNode defective2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Дефект 4222"));
        TreeNode discount3 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Скидка 4221"));
        retail2.addChildren(defective2, discount3);

        TreeNode certificate = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Сертификат 4212"));
        TreeNode discount2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Скидка 4211"));
        goodsSale.addChildren(defectiveThing, exchange2, certificateBy, retail2, certificate, discount2);

        TreeNode others = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Прочее 4701"));
        TreeNode writeOff = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Списание на ЗП 4401"));
        TreeNode writeOffCosts = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Списание на расходы 4630"));
        TreeNode defectiveStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Брак 4670"));
        TreeNode competition2 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Конкурс 4640"));
        TreeNode punch = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Группа PUNCH 4642"));
        TreeNode myaso = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Группа МЯСО 4641"));
        TreeNode internetCompetition = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Интернет 4643"));
        TreeNode externalCompetition = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Сторонний 4644"));
        competition2.addChildren(punch, myaso, internetCompetition, externalCompetition);


        TreeNode forCustomers = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "На привлечение клиентов 4690"));
        TreeNode organizationCoats = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Расходы организации 4680"));
        TreeNode advertising = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Реклама 4660"));
        TreeNode video = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Видео 4662"));
        TreeNode gift = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Подарок моделям 4663"));
        TreeNode photo = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Фото 4661"));
        advertising.addChildren(video, gift, photo);

        TreeNode sponsor = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Спонсорство 4650"));
        TreeNode concerts = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Концерты 4653"));
        TreeNode progress = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Развитие 4654"));
        TreeNode festival = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Фестивали 4652"));
        TreeNode endorsing = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Эндорсинг 4651"));
        sponsor.addChildren(concerts, progress, festival, endorsing);

        writeOffCosts.addChildren(defectiveStuff, competition2, forCustomers, organizationCoats, advertising, sponsor);

        TreeNode writeOffLack = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Списание недостач 4620"));
        TreeNode steal = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Кража 4622"));
        TreeNode postingErrors = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибки оприходования 4623"));
        TreeNode otherKindError = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Ошибки пересортицы 4624"));
        TreeNode lost = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_drive_file, "Утеря 4621"));
        writeOffLack.addChildren(steal, postingErrors, otherKindError, lost);


        productConsumption.addChildren(returnSuppliers, wage, otherKindStuff, discount, goodsSale,
                others, writeOff, writeOffCosts, writeOffLack);

        root.addChildren(parcels, parishFunds, arrivalOfGoods, cashOutflow, productConsumption);


        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(nodeClickListener);
        tView.setDefaultNodeLongClickListener(nodeLongClickListener);

        containerView.addView(tView.getView());

        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("tState");
            if (!TextUtils.isEmpty(state)) {
                tView.restoreState(state);
            }
        }
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.expandAll:
                tView.expandAll();
                break;

            case R.id.collapseAll:
                tView.collapseAll();
                break;
        }
        return true;
    }

    private int counter = 0;

    private void fillDownloadsFolder(TreeNode node) {
        TreeNode downloads = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Downloads" + (counter++)));
        node.addChild(downloads);
        if (counter < 5) {
            fillDownloadsFolder(downloads);
        }
    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
//            statusBar.setText("Last clicked: " + item.text);
        }
    };

    private TreeNode.TreeNodeLongClickListener nodeLongClickListener = new TreeNode.TreeNodeLongClickListener() {
        @Override
        public boolean onLongClick(TreeNode node, Object value) {
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            Toast.makeText(mContext, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show();
            CharSequence text = getFolderId(item.text);
            copyToClipboard(text);
            return true;
        }
    };


    /*
    work with String. get last 4 chars and add '@' before
     */
    private String getFolderId(String string) {
        String text = string;
        if (text.length() > 4) {
            return "@" + text.substring(text.length() - 4);
        } else {
            Toast.makeText(mContext, "word has less than 3 characters!", Toast.LENGTH_SHORT).show();
        }

        return text;
    }

    private void copyToClipboard(CharSequence text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tState", tView.getSaveState());
    }
}