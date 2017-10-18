package com.alxgrk.bachelorarbeit.accounts;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alxgrk.bachelorarbeit.R;
import com.alxgrk.bachelorarbeit.hateoas.Link;
import com.alxgrk.bachelorarbeit.hateoas.PossibleRelation;
import com.alxgrk.bachelorarbeit.root.RootFragment;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

import static com.alxgrk.bachelorarbeit.hateoas.PossibleRelation.SELF;

public class AccountUi {

    @Getter
    private List<Button> uiButtons = Lists.newArrayList();

    @Getter
    private List<ConstraintLayout> uiAccountEntries = Lists.newArrayList();

    private final AccountsFragment fragment;

    private final List<AccountButton> buttonSpecs;

    private final List<Account> accounts;

    @lombok.Builder(builderClassName = "InternalBuilder", builderMethodName = "internalBuilder")
    private AccountUi(AccountsFragment fragment, @Singular List<AccountButton> buttonSpecs,
                      @Singular List<Account> accounts) {
        this.fragment = fragment;
        this.buttonSpecs = buttonSpecs;
        this.accounts = accounts;
    }

    private void createAccountEntries() {
        Function<Account, ConstraintLayout> accountEntryCreationFuntion = acc -> {
            ConstraintLayout result = (ConstraintLayout) fragment.getLayoutInflater()
                    .inflate(R.layout.account_entry, null);

            TextView tvName = result.findViewById(R.id.tv_name);
            tvName.setText(acc.getName());

            TextView tvSurname = result.findViewById(R.id.tv_surname);
            tvSurname.setText(acc.getSurname());

            TextView tvUsername = result.findViewById(R.id.tv_username);
            tvUsername.setText(acc.getUsername());

            Collection<Link> orgLink = Collections2.filter(acc.getLinks(),
                    l -> PossibleRelation.ORGANIZATION.toString().equalsIgnoreCase(l.getRel()));
            if (1 == orgLink.size()) {
                Button btnOrg = result.findViewById(R.id.btn_org);
                btnOrg.setVisibility(View.VISIBLE);
                btnOrg.setText(PossibleRelation.ORGANIZATION.toString());
            }

            Collection<Link> resourcesLink = Collections2.filter(acc.getLinks(),
                    l -> PossibleRelation.RESOURCES.toString().equalsIgnoreCase(l.getRel()));
            if (1 == resourcesLink.size()) {
                Button btnRes = result.findViewById(R.id.btn_resources);
                btnRes.setVisibility(View.VISIBLE);
                btnRes.setText(PossibleRelation.RESOURCES.toString());
            }

            return result;
        };

        uiAccountEntries = Lists.newArrayList(Collections2.transform(accounts, accountEntryCreationFuntion));
    }

    private void createButtons() {
        List<String> expectedRels = Lists.newArrayList(SELF.toString());

        Collection<AccountButton> filtered = Collections2.filter(buttonSpecs,
                rb -> expectedRels.contains(rb.getDisplayText()));

        Function<AccountButton, Button> buttonCreationFunction = rb -> {
            PossibleRelation relation = PossibleRelation.getBy(rb.getDisplayText());

            switch (relation) {
                case SELF:
                    return createButtonWith("Reload", view -> { // TODO
                    });
                default:
                    // will never be passed due to filtering beforehand
                    return null;
            }
        };
        uiButtons = Lists.newArrayList(Collections2.transform(filtered, buttonCreationFunction));
    }

    private Button createFollowButton(AccountButton rb, View.OnClickListener onClick) {
        return createButtonWith(rb.getDisplayText(), onClick);
    }

    private Button createButtonWith(String displayText, View.OnClickListener onClick) {
        Button button = new Button(fragment.getContext());
        button.setText(displayText);
        button.setOnClickListener(onClick);
        return button;
    }

    public static Builder builder(AccountsFragment fragment) {
        return new Builder(fragment);
    }

    public static class Builder extends InternalBuilder {
        Builder(AccountsFragment fragment) {
            super();
            fragment(fragment);
        }

        @Override
        public AccountUi build() {
            AccountUi rootUi = super.build();
            rootUi.createButtons();
            rootUi.createAccountEntries();
            return rootUi;
        }
    }

    @Value
    public static class AccountButton {
        @NonNull
        final String displayText;

        @NonNull
        final String href;
    }
}