package com.vsevolod.swipe.addphoto.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.vsevolod.flowstreelibrary.model.TreeNode;
import com.vsevolod.swipe.addphoto.R;
import com.vsevolod.swipe.addphoto.adapter.AutoCompleteAdapter;
import com.vsevolod.swipe.addphoto.asyncTask.CommitTask;
import com.vsevolod.swipe.addphoto.config.Constants;
import com.vsevolod.swipe.addphoto.config.MyApplication;
import com.vsevolod.swipe.addphoto.config.PathConverter;
import com.vsevolod.swipe.addphoto.config.RealmHelper;
import com.vsevolod.swipe.addphoto.holder.IconTreeItemHolder;
import com.vsevolod.swipe.addphoto.model.realm.DataModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import it.sephiroth.android.library.picasso.Picasso;
import okhttp3.MediaType;
import okhttp3.RequestBody;

// TODO: 15.04.17 handle intents getting (camera photo, gallery photo, share  photo)
public class AddingActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private final int IMAGE_QUALITY = 40;
    //    private AndroidTreeView tView; //to add AndroidTreeView change "setContentView(R.layout.activity_adding);"
    private RealmHelper mRealmHelper = new RealmHelper();
    private AutoCompleteTextView mAutoCompleteTextView;
    private EditText mEditText;
    private String path = null;
    private String text;
    private long mLastClickTime = 0;
    private LocationTracker mTracker;
    private Location mLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        mRealmHelper.open();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.adding_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setLogo(R.drawable.logo);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith(Constants.INTENT_KEY_PATH)) {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                path = PathConverter.getFullPath(imageUri);
            }
        } else {
            path = getIntent().getStringExtra(Constants.INTENT_KEY_PATH);
        }

        ImageView mImageView = (ImageView) findViewById(R.id.adding_image_view);
        Picasso.with(this).load(path).into(mImageView);

//        setFlowsTree(savedInstanceState);
        mAutoCompleteTextView =
                (AutoCompleteTextView) findViewById(R.id.adding_auto_complete);
        mAutoCompleteTextView.setAdapter(new AutoCompleteAdapter());
        mAutoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mAutoCompleteTextView.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mEditText = (EditText) findViewById(R.id.adding_edit_text);
        mEditText.setImeOptions(EditorInfo.IME_ACTION_GO);
        mEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    checkLastClick();
                    handled = true;
                }
                return handled;
            }
        });
        setLocationTracker();
    }

    private void setLocationTracker() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            TrackerSettings settings =
                    new TrackerSettings()
                            .setUseGPS(true)
                            .setUseNetwork(true)
                            .setUsePassive(true)
                            .setTimeBetweenUpdates(1);

            mTracker = new LocationTracker(MyApplication.getAppContext(), settings) {

                @Override
                public void onLocationFound(@NonNull Location location) {
                    // Do some stuff when a new GPS Location has been found
                    Log.d(TAG, "onLocationFound");
                    mLocation = location;
                    stopListening();
                }

                @Override
                public void onTimeout() {
                    Log.d(TAG, "onTimeout");
                }
            };
            mTracker.startListening();
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (mTracker != null) {
            mTracker.stopListening();
        }
        mRealmHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        setLocationTracker();
        mRealmHelper.open();
        super.onResume();
    }

    private void decodeImage() {
        Log.d(TAG, "decodeImage");
        File imageFile = new File(path);
        text = mAutoCompleteTextView.getText().toString();
        final int THUMB_SIZE = Constants.THUMB_SIZE;
        if (imageFile.exists()) {
            prefixValidation();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap thumbImage = ThumbnailUtils.extractThumbnail(
                    BitmapFactory.decodeFile(imageFile.getAbsolutePath()), THUMB_SIZE, THUMB_SIZE);
            thumbImage.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, stream);
            byte[] byteArray = stream.toByteArray();
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveDataToRealm(byteArray, path);
            uploadImage(imageFile);
        } else {
            Log.d(TAG, "addImage: file is not exist");
        }
    }

    private void uploadImage(File imageFile) {
        //image uploading task
//        RequestBody reqFile = RequestBody.create(MediaType.parse(Constants.MEDIA_TYPE_IMAGE), imageFile);
//        CommitTask task = new CommitTask();
//        task.execute(reqFile);
    }

    private void prefixValidation() {
        Log.d(TAG, "prefixValidation");
        if (!mRealmHelper.isValid(text)) {
            String error = getResources().getString(R.string.choose_tag);
            mAutoCompleteTextView.setError(error);
        }
    }

    private void saveDataToRealm(@NonNull byte[] byteArray, @NonNull String photoUri) {
        Log.d(TAG, "saveDataToRealm");

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Date date = new Date();
        SimpleDateFormat searchDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        searchDateFormat.setTimeZone(timeZone);
        String searchDate = searchDateFormat.format(date.getTime());
        String comment = mEditText.getText().toString();
        text = mAutoCompleteTextView.getText().toString();
        String prefix = text.substring(text.length() - 4); //4 is a prefix length
        String name = text.substring(0, text.length() - 6); //6 is a prefix length + @ + space
        String prefixID = mRealmHelper.getPrefixID(prefix);
        double latitude = mLocation != null ? mLocation.getLatitude() : 404; // 404 is a random number to set when app can't track location
        double longitude = mLocation != null ? mLocation.getLongitude() : 404;

        DataModel model = new DataModel(
                searchDate,
                prefix,
                name,
                comment,
                photoUri,
                byteArray,
                latitude,
                longitude,
                prefixID,
                date
        );

        mRealmHelper.save(model);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_adding, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case R.id.send:
                checkLastClick();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkLastClick() {
        Log.d(TAG, "checkLastClick");
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        decodeImage();

    }

    private TreeNode.TreeNodeClickListener nodeClickListener = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            Log.d(TAG, "TreeNode.TreeNodeClickListener onClick");
            IconTreeItemHolder.IconTreeItem item = (IconTreeItemHolder.IconTreeItem) value;
            text = item.text;
        }
    };

    private void setFlowsTree(Bundle savedInstanceState) {
        Log.d(TAG, "setFlowsTree");
        ViewGroup containerView = (ViewGroup) findViewById(R.id.container);

        TreeNode root = TreeNode.root();
        TreeNode parcels = new TreeNode(new IconTreeItemHolder.IconTreeItem("Посылки 5000"));
        TreeNode parishFunds = new TreeNode(new IconTreeItemHolder.IconTreeItem("Приход денежных средств 1000"));
        TreeNode arrivalOfGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("Приход товара 3000"));
        TreeNode cashOutflow = new TreeNode(new IconTreeItemHolder.IconTreeItem("Расход денежных средств 2000"));
        TreeNode productConsumption = new TreeNode(new IconTreeItemHolder.IconTreeItem("Расход товара 4000"));

        TreeNode inBox = new TreeNode(new IconTreeItemHolder.IconTreeItem("Входящие 5001"));
        TreeNode fromDropShippingBuyers = new TreeNode(new IconTreeItemHolder.IconTreeItem("От дроповых покупателей 5370"));
        TreeNode refunds = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты 5371"));
        TreeNode money = new TreeNode(new IconTreeItemHolder.IconTreeItem("Деньги 5374"));
        TreeNode exchanges = new TreeNode(new IconTreeItemHolder.IconTreeItem("Обмены 5372"));
        TreeNode renouncement = new TreeNode(new IconTreeItemHolder.IconTreeItem("Отказ 5373"));
        fromDropShippingBuyers.addChildren(refunds, money, exchanges, renouncement);

        TreeNode fromSomeOne = new TreeNode(new IconTreeItemHolder.IconTreeItem("От кого-то 5770"));
        TreeNode notTargetLoadsFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem("Не целевые грузы (личные) 5771"));
        fromSomeOne.addChildren(notTargetLoadsFROM);

        TreeNode fromWholesaleBuyers = new TreeNode(new IconTreeItemHolder.IconTreeItem("От поставщиков 5170"));
        TreeNode refundsRealizableGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты реализационного товара 5382"));
        TreeNode refundsDefectiveGoodsFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты уже купленного брака 5381"));
        TreeNode exchangeOfGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("Обмены купленного на другой 5383"));
        fromWholesaleBuyers.addChildren(refundsRealizableGoods, refundsDefectiveGoodsFROM, exchangeOfGoods);

        TreeNode fromSuppliers = new TreeNode(new IconTreeItemHolder.IconTreeItem("От оптовых покупателей 5380"));
        TreeNode documentsFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem("Документы 5173"));
        TreeNode sampleFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem("Образцы 5172"));
        TreeNode deliveryFROM = new TreeNode(new IconTreeItemHolder.IconTreeItem("Поставки 5171"));
        fromSuppliers.addChildren(documentsFROM, sampleFROM, deliveryFROM);


        TreeNode fromRetailCustomers = new TreeNode(new IconTreeItemHolder.IconTreeItem("От розничных покупателей 5270"));
        TreeNode retailRefunds = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты 5271"));
        TreeNode retailMoney = new TreeNode(new IconTreeItemHolder.IconTreeItem("Деньги 5274"));
        TreeNode retailExchanges = new TreeNode(new IconTreeItemHolder.IconTreeItem("Обмены 5272"));
        TreeNode retailRenouncement = new TreeNode(new IconTreeItemHolder.IconTreeItem("Отказ 5273"));
        fromRetailCustomers.addChildren(retailRefunds, retailMoney, retailExchanges, retailRenouncement);

        inBox.addChildren(fromDropShippingBuyers, fromSomeOne, fromWholesaleBuyers, fromSuppliers, fromRetailCustomers);

        TreeNode outBox = new TreeNode(new IconTreeItemHolder.IconTreeItem("Исходящие 5002"));

        TreeNode toFutureCounterparties = new TreeNode(new IconTreeItemHolder.IconTreeItem("К будущим контрагентам 5390"));
        TreeNode samplesOfGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("Образцы продукции и тд. 5391"));
        toFutureCounterparties.addChildren(samplesOfGoods);

        TreeNode toDropShippingBuyers = new TreeNode(new IconTreeItemHolder.IconTreeItem("К дроповым покупателям 5320"));
        TreeNode goodsExchange = new TreeNode(new IconTreeItemHolder.IconTreeItem("Обмен товара 5322"));
        TreeNode goods = new TreeNode(new IconTreeItemHolder.IconTreeItem("Товары 5321"));
        toDropShippingBuyers.addChildren(goodsExchange, goods);

        TreeNode toSomeOne = new TreeNode(new IconTreeItemHolder.IconTreeItem("К кому-либо 5720"));
        TreeNode notTargetLoadsTO = new TreeNode(new IconTreeItemHolder.IconTreeItem("Не целевые грузы (личные) 5721"));
        toSomeOne.addChildren(notTargetLoadsTO);

        TreeNode toWholesaleBuyers = new TreeNode(new IconTreeItemHolder.IconTreeItem("К оптовым покупателям 5330"));
        TreeNode goodsExchangeTO = new TreeNode(new IconTreeItemHolder.IconTreeItem("Обмены купленного или брака 5332"));
        TreeNode deliveryTO = new TreeNode(new IconTreeItemHolder.IconTreeItem("Товары (поставки) 5331"));
        toWholesaleBuyers.addChildren(goodsExchangeTO, deliveryTO);

        TreeNode toSuppliers = new TreeNode(new IconTreeItemHolder.IconTreeItem("К поставщикам 5120"));
        TreeNode refundsDefectiveGoodsTO = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возврат брака 5121"));
        TreeNode refundsDocumentsTO = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты документов 5124"));
        TreeNode refundsSamplesTO = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты образцов 5122"));
        TreeNode refundsMassTO = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты товарных масс 5123"));
        toSuppliers.addChildren(refundsDefectiveGoodsTO, refundsDocumentsTO, refundsSamplesTO, refundsMassTO);

        TreeNode toRetailCustomers = new TreeNode(new IconTreeItemHolder.IconTreeItem("К розничным покупателям 5220"));
        TreeNode goodsExchangeRetail = new TreeNode(new IconTreeItemHolder.IconTreeItem("Обмен товара 5222"));
        TreeNode goodsTO = new TreeNode(new IconTreeItemHolder.IconTreeItem("Товары 5221"));
        toRetailCustomers.addChildren(goodsExchangeRetail, goodsTO);

        outBox.addChildren(toFutureCounterparties, toDropShippingBuyers, toSomeOne, toWholesaleBuyers, toSuppliers, toRetailCustomers);

        TreeNode movements = new TreeNode(new IconTreeItemHolder.IconTreeItem("Перемещения 5600"));
        TreeNode refundsSurpluses = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возврат излишков 5671"));
        TreeNode materialValues = new TreeNode(new IconTreeItemHolder.IconTreeItem("Материальные ценности 5623"));
        TreeNode notTarget = new TreeNode(new IconTreeItemHolder.IconTreeItem("Не целевые грузы 5675"));
        TreeNode documentsPackages = new TreeNode(new IconTreeItemHolder.IconTreeItem("Пакеты документов 5622"));
        TreeNode goodsDelivery = new TreeNode(new IconTreeItemHolder.IconTreeItem("Поставки товаров 5621"));
        TreeNode partsOfPrefabricatedPremises = new TreeNode(new IconTreeItemHolder.IconTreeItem("Части сборных посылок 5672"));
        movements.addChildren(refundsSurpluses, materialValues, notTarget, documentsPackages, goodsDelivery, partsOfPrefabricatedPremises);

        parcels.addChildren(inBox, outBox, movements);

        TreeNode refundFromSupplier = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возврат средств от поставщика 1100"));

        TreeNode proceedsOfSales = new TreeNode(new IconTreeItemHolder.IconTreeItem("Выручка от продаж 1200"));
        TreeNode retailStores = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи розничных магазинов 1210"));
        TreeNode supplementCertificate = new TreeNode(new IconTreeItemHolder.IconTreeItem("Доплата к сертификату 1215"));
        TreeNode salesForCashNoCheck = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи за наличные по кассе без чека 1213"));
        TreeNode salesForCashWithCheck = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи за наличные с товарным чеком 1212"));
        TreeNode salesForCashDiscount = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи за наличные со скидкой 1211"));
        TreeNode salesForCard = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи через терминал 1214"));
        retailStores.addChildren(supplementCertificate, salesForCashNoCheck, salesForCashWithCheck, salesForCashDiscount, salesForCard);

        TreeNode centralOffice = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи центрального офиса 1230"));
        TreeNode vk = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи группы ВК 1232"));
        TreeNode callCentre = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи кол-центра 1231"));
        TreeNode dropAndWholeSale = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи опт/дроп 1233"));
        TreeNode site = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажи сайта 1234"));
        centralOffice.addChildren(vk, callCentre, dropAndWholeSale, site);

        proceedsOfSales.addChildren(retailStores, centralOffice);

        TreeNode movementInOrganization = new TreeNode(new IconTreeItemHolder.IconTreeItem("Движение в организации 1600"));
        TreeNode innerTransactions = new TreeNode(new IconTreeItemHolder.IconTreeItem("Внутренние транзакции входящие 1601"));
        TreeNode refundEmployee = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возврат средств сотрудником 1602"));
        TreeNode collectionCard = new TreeNode(new IconTreeItemHolder.IconTreeItem("Инкассация наличных на карту 1605"));
        TreeNode transfersFromOtherCards = new TreeNode(new IconTreeItemHolder.IconTreeItem("Переводы с других карт организации 1604"));
        TreeNode admissionToAcquiringAccount = new TreeNode(new IconTreeItemHolder.IconTreeItem("Поступление на эквайринговый счет 1606"));
        TreeNode arrivingExternalAccount = new TreeNode(new IconTreeItemHolder.IconTreeItem("Поступление с внешнего счета 1603"));
        movementInOrganization.addChildren(innerTransactions, refundEmployee, collectionCard, transfersFromOtherCards, admissionToAcquiringAccount, arrivingExternalAccount);

        TreeNode nonCoreIncome = new TreeNode(new IconTreeItemHolder.IconTreeItem("Доход от неосновной деятельности 1300"));
        TreeNode services = new TreeNode(new IconTreeItemHolder.IconTreeItem("Предоставление услуг 1302"));
        TreeNode assetsSale = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажа основных средств 1301"));
        nonCoreIncome.addChildren(services, assetsSale);

        TreeNode arrivalOfFounders = new TreeNode(new IconTreeItemHolder.IconTreeItem("Приход инвестиций учредителей 1510"));

        parishFunds.addChildren(refundFromSupplier, proceedsOfSales, movementInOrganization, nonCoreIncome, arrivalOfFounders);

        TreeNode refund = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возврат 3260"));
        TreeNode defectiveGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("Дефект на товаре 3273"));
        TreeNode wrongSize = new TreeNode(new IconTreeItemHolder.IconTreeItem("Другой размер (нет обмена) 3271"));
        TreeNode notMatchPhoto = new TreeNode(new IconTreeItemHolder.IconTreeItem("Не соответствует фото 3272"));
        refund.addChildren(defectiveGoods, wrongSize, notMatchPhoto);

        TreeNode exchange = new TreeNode(new IconTreeItemHolder.IconTreeItem("Обмен 3230"));
        TreeNode otherSize = new TreeNode(new IconTreeItemHolder.IconTreeItem("Другой размер 3242"));
        TreeNode otherGood = new TreeNode(new IconTreeItemHolder.IconTreeItem("Другой товар 3241"));
        TreeNode otherColor = new TreeNode(new IconTreeItemHolder.IconTreeItem("Другой цвет 3243"));
        TreeNode saleError = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка проведения 3244"));
        exchange.addChildren(otherSize, otherGood, otherColor, saleError);

        TreeNode posting = new TreeNode(new IconTreeItemHolder.IconTreeItem("Оприходование 3620"));
        TreeNode kindError = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка пересортицы 3622"));
        TreeNode writeOffError = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка списания 3621"));
        posting.addChildren(kindError, writeOffError);

        TreeNode fromEmployee = new TreeNode(new IconTreeItemHolder.IconTreeItem("От сотрудника 3400"));

        TreeNode saleCancel = new TreeNode(new IconTreeItemHolder.IconTreeItem("Отмена продажи 3210"));
        TreeNode customerRejection = new TreeNode(new IconTreeItemHolder.IconTreeItem("Отказ клиента 3213"));
        TreeNode noSuchGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("Отсутствует товар 3211"));
        TreeNode behaviorError = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка проведения 3212"));
        saleCancel.addChildren(customerRejection, noSuchGoods, behaviorError);
        fromEmployee.addChildren(saleCancel);

        TreeNode deliveryError = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка отгрузки 3220"));
        TreeNode goodsTransferError = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка передачи товара 3222"));
        TreeNode packerError = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка упаковщика 3221"));
        deliveryError.addChildren(goodsTransferError, packerError);

        TreeNode transfer = new TreeNode(new IconTreeItemHolder.IconTreeItem("Перемещение 3630"));
        TreeNode fixing = new TreeNode(new IconTreeItemHolder.IconTreeItem("Корректировка 3634"));
        TreeNode storeToStore = new TreeNode(new IconTreeItemHolder.IconTreeItem("Магазин-Магазин 3633"));
        TreeNode storeToStock = new TreeNode(new IconTreeItemHolder.IconTreeItem("Магазин-Склад 3632"));
        TreeNode stockToStore = new TreeNode(new IconTreeItemHolder.IconTreeItem("Склад-Магазин 3631"));
        transfer.addChildren(fixing, storeToStore, storeToStock, stockToStore);

        TreeNode otherKind = new TreeNode(new IconTreeItemHolder.IconTreeItem("Пересортица 3610"));
        TreeNode postingError = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка оприходования 3611"));
        otherKind.addChildren(postingError);

        TreeNode supply = new TreeNode(new IconTreeItemHolder.IconTreeItem("Поступление 3110"));
        TreeNode ransom = new TreeNode(new IconTreeItemHolder.IconTreeItem("Выкуп 3111"));
        TreeNode fixingSupply = new TreeNode(new IconTreeItemHolder.IconTreeItem("Корректировка 3113"));
        TreeNode retail = new TreeNode(new IconTreeItemHolder.IconTreeItem("Реализация 3112"));
        supply.addChildren(ransom, fixingSupply, retail);

        TreeNode other = new TreeNode(new IconTreeItemHolder.IconTreeItem("Прочее 3710"));
        arrivalOfGoods.addChildren(refund, exchange, posting, fromEmployee, deliveryError, transfer, otherKind, supply, other);

        TreeNode returnFoundersInvestments = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возврат инвестиций учредителей 2510"));

        TreeNode customerReturns = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты клиентам 2200"));
        TreeNode customersCardReturns = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты клиентам на карту 2202"));
        TreeNode customersCashReturns = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты клиентам наличными 2203"));
        TreeNode wholeSaleCustomersReturns = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возвраты клиентам опт 2201"));
        customerReturns.addChildren(customersCardReturns, customersCashReturns, wholeSaleCustomersReturns);

        TreeNode organizationMovement = new TreeNode(new IconTreeItemHolder.IconTreeItem("Движение в организации 2600"));
        TreeNode externalTransactions = new TreeNode(new IconTreeItemHolder.IconTreeItem("Внутренние транзакции исходящие 2601"));
        TreeNode externalAccountTransfer = new TreeNode(new IconTreeItemHolder.IconTreeItem("Выведение на внешний счет 2603"));
        TreeNode cashRecessForCollections = new TreeNode(new IconTreeItemHolder.IconTreeItem("Выемка наличных для инкассации 2605"));
        TreeNode cashRecessForEmployee = new TreeNode(new IconTreeItemHolder.IconTreeItem("Выемка средств сотрудником 2602"));
        TreeNode organizationOtherCardsTransactions = new TreeNode(new IconTreeItemHolder.IconTreeItem("Переводы на другие карты организации 2604"));
        organizationMovement.addChildren(externalTransactions, externalAccountTransfer, cashRecessForCollections,
                cashRecessForEmployee, organizationOtherCardsTransactions);

        TreeNode assetsPurchase = new TreeNode(new IconTreeItemHolder.IconTreeItem("Закупка основных средств 2370"));
        TreeNode library = new TreeNode(new IconTreeItemHolder.IconTreeItem("Библиотека 2377"));
        TreeNode cctv = new TreeNode(new IconTreeItemHolder.IconTreeItem("Видеонаблюдение 2374\n"));
        TreeNode tools = new TreeNode(new IconTreeItemHolder.IconTreeItem("Инструменты 2376"));
        TreeNode light = new TreeNode(new IconTreeItemHolder.IconTreeItem("Освещение 2375"));
        TreeNode officeEquipment = new TreeNode(new IconTreeItemHolder.IconTreeItem("Офисная техника 2371"));
        TreeNode saleFurniture = new TreeNode(new IconTreeItemHolder.IconTreeItem("Торговое оборудование 2372"));
        TreeNode photoVideoStudio = new TreeNode(new IconTreeItemHolder.IconTreeItem("Фото-видео-студия 2373"));
        assetsPurchase.addChildren(library, cctv, tools, light, officeEquipment, saleFurniture, photoVideoStudio);

        TreeNode paymentSuppliers = new TreeNode(new IconTreeItemHolder.IconTreeItem("Оплата поставщикам 2100"));
        TreeNode madeGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("За произведенный товар 2104"));
        TreeNode soldGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("За реализационный товар 2102"));
        TreeNode ownGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("За собственный товар 2101"));
        paymentSuppliers.addChildren(madeGoods, soldGoods, ownGoods);

        TreeNode softwareDevelopment = new TreeNode(new IconTreeItemHolder.IconTreeItem("Разработка ПО 2390"));
        TreeNode oneC = new TreeNode(new IconTreeItemHolder.IconTreeItem("Разработка 1С 2394"));
        TreeNode crm = new TreeNode(new IconTreeItemHolder.IconTreeItem("Разработка CRM 2391"));
        TreeNode additionalModules = new TreeNode(new IconTreeItemHolder.IconTreeItem("Разработка дополнительных модулей 2393"));
        TreeNode cite = new TreeNode(new IconTreeItemHolder.IconTreeItem("Разработка сайта 2392"));
        softwareDevelopment.addChildren(oneC, crm, additionalModules, cite);

        TreeNode currentExpenditure = new TreeNode(new IconTreeItemHolder.IconTreeItem("Текущие расходы 2320"));
        TreeNode rent = new TreeNode(new IconTreeItemHolder.IconTreeItem("Аренда 2310"));
        TreeNode roomRent = new TreeNode(new IconTreeItemHolder.IconTreeItem("Аренда помещения 2311"));
        TreeNode communalPayments = new TreeNode(new IconTreeItemHolder.IconTreeItem("Коммунальные платежи 2312"));
        TreeNode otherRentCosts = new TreeNode(new IconTreeItemHolder.IconTreeItem("Прочие затраты на аренду 2313"));
        rent.addChildren(roomRent, communalPayments, otherRentCosts);

        TreeNode bank = new TreeNode(new IconTreeItemHolder.IconTreeItem("Банк 2350"));
        TreeNode monthlyFee = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ежемесячная оплата 2354"));
        TreeNode liqPayPercent = new TreeNode(new IconTreeItemHolder.IconTreeItem("Процент за LiqPay-оплаты 2355"));
        TreeNode cashWithdrawalPercent = new TreeNode(new IconTreeItemHolder.IconTreeItem("Процент за снятие наличных 2352"));
        TreeNode acquiringPercent = new TreeNode(new IconTreeItemHolder.IconTreeItem("Процент за эквайринг 2351"));
        TreeNode transferPercent = new TreeNode(new IconTreeItemHolder.IconTreeItem("Процент при переводе 2353"));
        bank.addChildren(monthlyFee, liqPayPercent, cashWithdrawalPercent, acquiringPercent, transferPercent);

        TreeNode delivery = new TreeNode(new IconTreeItemHolder.IconTreeItem("Доставка 2340"));
        TreeNode courier = new TreeNode(new IconTreeItemHolder.IconTreeItem("Курьер 2341"));

        TreeNode novaPoshta = new TreeNode(new IconTreeItemHolder.IconTreeItem("Новая Почта 2380"));
        TreeNode purchase = new TreeNode(new IconTreeItemHolder.IconTreeItem("Доставка закупки 2383"));
        TreeNode toCustomer = new TreeNode(new IconTreeItemHolder.IconTreeItem("Доставка клиенту 2381"));
        TreeNode betweenStores = new TreeNode(new IconTreeItemHolder.IconTreeItem("Доставка между магазинами 2382"));
        novaPoshta.addChildren(purchase, toCustomer, betweenStores);

        TreeNode taxi = new TreeNode(new IconTreeItemHolder.IconTreeItem("Такси 2342"));
        delivery.addChildren(courier, novaPoshta, taxi);

        TreeNode taxes = new TreeNode(new IconTreeItemHolder.IconTreeItem("Налоги 2700"));
        TreeNode war = new TreeNode(new IconTreeItemHolder.IconTreeItem("Военный сбор 2701"));
        TreeNode ediniy = new TreeNode(new IconTreeItemHolder.IconTreeItem("Единый налог 2702"));
        TreeNode esv = new TreeNode(new IconTreeItemHolder.IconTreeItem("ЕСВ 2703"));
        TreeNode pdfo = new TreeNode(new IconTreeItemHolder.IconTreeItem("ПДФО 2704"));
        taxes.addChildren(war, ediniy, esv, pdfo);

        TreeNode consumablesAndServices = new TreeNode(new IconTreeItemHolder.IconTreeItem("Расходные материалы и услуги 2800"));
        TreeNode internet = new TreeNode(new IconTreeItemHolder.IconTreeItem("Интернет 2801"));
        TreeNode officeStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem("Канцелярия 2807"));
        TreeNode food = new TreeNode(new IconTreeItemHolder.IconTreeItem("Кафетерий 2808"));
        TreeNode outOfTown = new TreeNode(new IconTreeItemHolder.IconTreeItem("Командировки: транспорт и суточные 2803"));
        TreeNode outOfTownEvents = new TreeNode(new IconTreeItemHolder.IconTreeItem("Корпоративные мероприятия 2804"));
        TreeNode mobile = new TreeNode(new IconTreeItemHolder.IconTreeItem("Мобильная связь 2802"));
        TreeNode otherStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem("Остальное 2815"));
        TreeNode office = new TreeNode(new IconTreeItemHolder.IconTreeItem("Расходы бухгалтерии и юридические услуги 2809"));
        TreeNode employee = new TreeNode(new IconTreeItemHolder.IconTreeItem("Расходы по подбору сотрудников 2813"));
        TreeNode officeRepairs = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ремонт в магазинах и офисах 2814"));
        TreeNode toolsRepairs = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ремонт и обслуживание техники 2805"));
        TreeNode citeRepairs = new TreeNode(new IconTreeItemHolder.IconTreeItem("Текущие расходы на сайт 2811"));
        toolsRepairs.addChildren(citeRepairs);

        TreeNode clean = new TreeNode(new IconTreeItemHolder.IconTreeItem("Текущие расходы по клинингу 2812"));
        TreeNode cleanStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem("Хоз.товары 2806"));
        consumablesAndServices.addChildren(internet, officeStuff, food, outOfTown, outOfTownEvents, mobile,
                otherStuff, office, employee, officeRepairs, toolsRepairs, clean, cleanStuff);


        TreeNode costsOfEmployee = new TreeNode(new IconTreeItemHolder.IconTreeItem("Расходы на персонал 2400"));
        TreeNode bonus = new TreeNode(new IconTreeItemHolder.IconTreeItem("Бонусная выплата зарплаты 2411"));
        TreeNode it = new TreeNode(new IconTreeItemHolder.IconTreeItem("Зарплата айтишников 2408"));
        TreeNode accountant = new TreeNode(new IconTreeItemHolder.IconTreeItem("Зарплата бухгалтеров 2405"));
        TreeNode purchaser = new TreeNode(new IconTreeItemHolder.IconTreeItem("Зарплата закупщиков 2407"));
        TreeNode internetSaler = new TreeNode(new IconTreeItemHolder.IconTreeItem("Зарплата интернет-продавцов 2406"));
        TreeNode marketolog = new TreeNode(new IconTreeItemHolder.IconTreeItem("Зарплата маркетологов 2404"));
        TreeNode manager = new TreeNode(new IconTreeItemHolder.IconTreeItem("Зарплата менеджеров 2409"));
        TreeNode saler = new TreeNode(new IconTreeItemHolder.IconTreeItem("Зарплата продавцов магазинов 2402"));
        TreeNode stockWorker = new TreeNode(new IconTreeItemHolder.IconTreeItem("Зарплата работников склада 2403"));
        TreeNode remuneration = new TreeNode(new IconTreeItemHolder.IconTreeItem("Партнерское вознагражение ФОП 2401"));
        costsOfEmployee.addChildren(bonus, it, accountant, purchaser, internetSaler, marketolog, manager,
                saler, stockWorker, remuneration);

        TreeNode advertisingCosts = new TreeNode(new IconTreeItemHolder.IconTreeItem("Расходы на рекламу 2900"));
        TreeNode internetAndMedia = new TreeNode(new IconTreeItemHolder.IconTreeItem("Реклама в интернете и медиа 2901"));
        TreeNode competition = new TreeNode(new IconTreeItemHolder.IconTreeItem("Конкурсная реклама 2907"));
        TreeNode context = new TreeNode(new IconTreeItemHolder.IconTreeItem("Контекстная реклама в Гугл 2906"));
        TreeNode instagram = new TreeNode(new IconTreeItemHolder.IconTreeItem("Реклама в Инстаграмм 2902"));
        TreeNode VK = new TreeNode(new IconTreeItemHolder.IconTreeItem("Реклама в сообществах ВК 2904"));
        TreeNode sms = new TreeNode(new IconTreeItemHolder.IconTreeItem("Реклама СМС-рассылкой 2903"));
        TreeNode target = new TreeNode(new IconTreeItemHolder.IconTreeItem("Реклама таргетированная Вконтакте 2905"));
        internetAndMedia.addChildren(competition, context, instagram, VK, sms, target);

        TreeNode external = new TreeNode(new IconTreeItemHolder.IconTreeItem("Реклама внешняя 2911"));
        TreeNode billboard = new TreeNode(new IconTreeItemHolder.IconTreeItem("Афиши в вагонах метро 2915"));
        TreeNode info = new TreeNode(new IconTreeItemHolder.IconTreeItem("Информационные стенды и щиты 2917"));
        TreeNode metroboard = new TreeNode(new IconTreeItemHolder.IconTreeItem("Метроборды и метролайты 2914"));
        TreeNode transport = new TreeNode(new IconTreeItemHolder.IconTreeItem("Наземный транспорт 2912"));
        TreeNode otherThings = new TreeNode(new IconTreeItemHolder.IconTreeItem("Остальная реклама 2923"));
        TreeNode leaflets = new TreeNode(new IconTreeItemHolder.IconTreeItem("Печать и раздача листовок 2922"));
        TreeNode billboardPrint = new TreeNode(new IconTreeItemHolder.IconTreeItem("Печать и расклейка афиш по городу 2916"));
        TreeNode shoppingCenter = new TreeNode(new IconTreeItemHolder.IconTreeItem("Рекламные плоскости в торговом центре 2921"));
        TreeNode cityLight = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ситилайты 2913"));
        TreeNode traffaret = new TreeNode(new IconTreeItemHolder.IconTreeItem("Трафареты на асфальте 2919"));
        TreeNode facade = new TreeNode(new IconTreeItemHolder.IconTreeItem("Фасадная реклама 2918"));
        external.addChildren(billboard, info, metroboard, transport, otherThings, leaflets, billboardPrint,
                shoppingCenter, cityLight, traffaret, facade);

        advertisingCosts.addChildren(internetAndMedia, external);


        currentExpenditure.addChildren(rent, bank, delivery, taxes, consumablesAndServices,
                costsOfEmployee, advertisingCosts);

        TreeNode foundersRecess = new TreeNode(new IconTreeItemHolder.IconTreeItem("Чистые выемки учредителей 2520"));

        cashOutflow.addChildren(returnFoundersInvestments, customerReturns, organizationMovement, assetsPurchase,
                paymentSuppliers, softwareDevelopment, currentExpenditure, foundersRecess);


        TreeNode returnSuppliers = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возврат поставщику 4110"));
        TreeNode defective = new TreeNode(new IconTreeItemHolder.IconTreeItem("Брак товара 4112"));
        TreeNode virtualReturn = new TreeNode(new IconTreeItemHolder.IconTreeItem("Виртуальный возврат 4115"));
        TreeNode balancesReturn = new TreeNode(new IconTreeItemHolder.IconTreeItem("Возврат остатков 4114"));
        TreeNode endOfSeason = new TreeNode(new IconTreeItemHolder.IconTreeItem("Конец сезона 4111"));
        TreeNode noSales = new TreeNode(new IconTreeItemHolder.IconTreeItem("Нет продаж (не ликвид) 4113"));
        returnSuppliers.addChildren(defective, virtualReturn, balancesReturn, endOfSeason, noSales);


        TreeNode wage = new TreeNode(new IconTreeItemHolder.IconTreeItem("Выемка на ЗП 4501"));
        TreeNode otherKindStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem("Пересортица 4610"));
        TreeNode error = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка оприходования 4611"));
        otherKindStuff.addChildren(error);

        TreeNode discount = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажа со скидкой 4502"));
        TreeNode goodsSale = new TreeNode(new IconTreeItemHolder.IconTreeItem("Продажа товара 4200"));
        TreeNode defectiveThing = new TreeNode(new IconTreeItemHolder.IconTreeItem("Дефект 4214"));
        TreeNode exchange2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Обмен 4230"));
        TreeNode wrongSize2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Другой размер 4232"));
        TreeNode wrongGoods = new TreeNode(new IconTreeItemHolder.IconTreeItem("Другой товар 4231"));
        TreeNode wrongColor2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Другой цвет 4233"));
        TreeNode postingError2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибка проведения 4234"));
        exchange2.addChildren(wrongSize2, wrongGoods, wrongColor2, postingError2);


        TreeNode certificateBy = new TreeNode(new IconTreeItemHolder.IconTreeItem("По сертификату 4213"));
        TreeNode retail2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Реализация 4220"));
        TreeNode defective2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Дефект 4222"));
        TreeNode discount3 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Скидка 4221"));
        retail2.addChildren(defective2, discount3);

        TreeNode certificate = new TreeNode(new IconTreeItemHolder.IconTreeItem("Сертификат 4212"));
        TreeNode discount2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Скидка 4211"));
        goodsSale.addChildren(defectiveThing, exchange2, certificateBy, retail2, certificate, discount2);

        TreeNode others = new TreeNode(new IconTreeItemHolder.IconTreeItem("Прочее 4701"));
        TreeNode writeOff = new TreeNode(new IconTreeItemHolder.IconTreeItem("Списание на ЗП 4401"));
        TreeNode writeOffCosts = new TreeNode(new IconTreeItemHolder.IconTreeItem("Списание на расходы 4630"));
        TreeNode defectiveStuff = new TreeNode(new IconTreeItemHolder.IconTreeItem("Брак 4670"));
        TreeNode competition2 = new TreeNode(new IconTreeItemHolder.IconTreeItem("Конкурс 4640"));
        TreeNode punch = new TreeNode(new IconTreeItemHolder.IconTreeItem("Группа PUNCH 4642"));
        TreeNode myaso = new TreeNode(new IconTreeItemHolder.IconTreeItem("Группа МЯСО 4641"));
        TreeNode internetCompetition = new TreeNode(new IconTreeItemHolder.IconTreeItem("Интернет 4643"));
        TreeNode externalCompetition = new TreeNode(new IconTreeItemHolder.IconTreeItem("Сторонний 4644"));
        competition2.addChildren(punch, myaso, internetCompetition, externalCompetition);

        TreeNode forCustomers = new TreeNode(new IconTreeItemHolder.IconTreeItem("На привлечение клиентов 4690"));
        TreeNode organizationCoats = new TreeNode(new IconTreeItemHolder.IconTreeItem("Расходы организации 4680"));
        TreeNode advertising = new TreeNode(new IconTreeItemHolder.IconTreeItem("Реклама 4660"));
        TreeNode video = new TreeNode(new IconTreeItemHolder.IconTreeItem("Видео 4662"));
        TreeNode gift = new TreeNode(new IconTreeItemHolder.IconTreeItem("Подарок моделям 4663"));
        TreeNode photo = new TreeNode(new IconTreeItemHolder.IconTreeItem("Фото 4661"));
        advertising.addChildren(video, gift, photo);

        TreeNode sponsor = new TreeNode(new IconTreeItemHolder.IconTreeItem("Спонсорство 4650"));
        TreeNode concerts = new TreeNode(new IconTreeItemHolder.IconTreeItem("Концерты 4653"));
        TreeNode progress = new TreeNode(new IconTreeItemHolder.IconTreeItem("Развитие 4654"));
        TreeNode festival = new TreeNode(new IconTreeItemHolder.IconTreeItem("Фестивали 4652"));
        TreeNode endorsing = new TreeNode(new IconTreeItemHolder.IconTreeItem("Эндорсинг 4651"));
        sponsor.addChildren(concerts, progress, festival, endorsing);

        writeOffCosts.addChildren(defectiveStuff, competition2, forCustomers, organizationCoats, advertising, sponsor);

        TreeNode writeOffLack = new TreeNode(new IconTreeItemHolder.IconTreeItem("Списание недостач 4620"));
        TreeNode steal = new TreeNode(new IconTreeItemHolder.IconTreeItem("Кража 4622"));
        TreeNode postingErrors = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибки оприходования 4623"));
        TreeNode otherKindError = new TreeNode(new IconTreeItemHolder.IconTreeItem("Ошибки пересортицы 4624"));
        TreeNode lost = new TreeNode(new IconTreeItemHolder.IconTreeItem("Утеря 4621"));
        writeOffLack.addChildren(steal, postingErrors, otherKindError, lost);


        productConsumption.addChildren(returnSuppliers, wage, otherKindStuff, discount, goodsSale,
                others, writeOff, writeOffCosts, writeOffLack);

        root.addChildren(parcels, parishFunds, arrivalOfGoods, cashOutflow, productConsumption);


//        tView = new AndroidTreeView(this, root);
//        tView.setDefaultAnimation(true);
//        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
//        tView.setDefaultViewHolder(IconTreeItemHolder.class);
//        tView.setDefaultNodeClickListener(nodeClickListener);
//
//        containerView.addView(tView.getView());
//
//        if (savedInstanceState != null) {
//            String state = savedInstanceState.getString("tState");
//            if (!TextUtils.isEmpty(state)) {
//                tView.restoreState(state);
//            }
//        }
    }
}